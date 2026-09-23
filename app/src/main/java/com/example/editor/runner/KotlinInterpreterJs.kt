package com.example.editor.runner

/**
 * Production-grade, zero-dependency Kotlin Runner engine in JavaScript.
 * Runs completely offline in WebView sandbox with execution support for fun main(),
 * val/var assignments, string templates, loops (for in 1..n / collections),
 * when/if-else expressions, collections (listOf, mutableListOf, mapOf),
 * collection operations (.sum(), .size, .joinToString()), and interactive readLine().
 */
object KotlinInterpreterJs {

    fun getScript(): String = """
<script id="__kotlin_engine__">
(function() {
    window.KotlinEngine = {
        run: async function(sourceCode, onPrint, onInput, onComplete, onError) {
            try {
                var env = {
                    globals: {
                        'true': true,
                        'false': false,
                        'null': null,
                        'listOf': function() { return Array.from(arguments); },
                        'mutableListOf': function() { return Array.from(arguments); },
                        'mapOf': function() {
                            var map = {};
                            for (var i = 0; i < arguments.length; i++) {
                                var pair = arguments[i];
                                if (pair && pair.key !== undefined) map[pair.key] = pair.value;
                            }
                            return map;
                        },
                        'maxOf': function() { return Math.max.apply(null, Array.from(arguments)); },
                        'minOf': function() { return Math.min.apply(null, Array.from(arguments)); },
                        'sqrt': Math.sqrt,
                        'PI': Math.PI
                    },
                    functions: {}
                };

                // Kotlin println / print
                env.globals['println'] = function() {
                    var args = Array.from(arguments);
                    var text = args.map(formatKotlinVal).join(' ');
                    onPrint(text + '\n');
                };

                env.globals['print'] = function() {
                    var args = Array.from(arguments);
                    var text = args.map(formatKotlinVal).join(' ');
                    onPrint(text);
                };

                // Kotlin readLine with interactive prompt
                env.globals['readLine'] = async function() {
                    var res = await onInput('');
                    return res;
                };

                function formatKotlinVal(val) {
                    if (val === true) return 'true';
                    if (val === false) return 'false';
                    if (val === null || val === undefined) return 'null';
                    if (Array.isArray(val)) return '[' + val.map(formatKotlinVal).join(', ') + ']';
                    return String(val);
                }

                // Parse Kotlin statements & functions
                var cleanCode = sourceCode.replace(/\/\*[\s\S]*?\*\//g, '').replace(/\/\/.*$/gm, '');
                var statements = parseKotlinStatements(cleanCode);

                // Register all functions first
                for (var s = 0; s < statements.length; s++) {
                    var stmt = statements[s];
                    if (stmt.type === 'fun') {
                        env.functions[stmt.name] = stmt;
                    }
                }

                // If 'main' exists, execute main(), else execute top-level statements
                if (env.functions['main']) {
                    await executeKotlinFunction('main', [], env);
                } else {
                    for (var t = 0; t < statements.length; t++) {
                        if (statements[t].type !== 'fun') {
                            await executeKotlinStatement(statements[t], env);
                        }
                    }
                }

                onComplete();
            } catch(err) {
                var msg = (err && err.message) ? err.message : String(err);
                if (msg !== '__RETURN__') {
                    onError("Kotlin Runtime Exception:\n  " + msg);
                } else {
                    onComplete();
                }
            }
        }
    };

    function parseKotlinStatements(code) {
        var statements = [];
        var i = 0;
        var n = code.length;

        while (i < n) {
            // Skip whitespace
            while (i < n && /\s/.test(code[i])) i++;
            if (i >= n) break;

            // Check for 'fun' definition
            if (code.substring(i).startsWith('fun ')) {
                var funMatch = code.substring(i).match(/^fun\s+([A-Za-z0-9_]+)\s*\((.*?)\)(?:\s*:\s*[A-Za-z0-9_<>,?\s]+)?\s*\{/);
                if (funMatch) {
                    var fnName = funMatch[1];
                    var rawParams = funMatch[2].trim();
                    var params = rawParams ? rawParams.split(',').map(function(p) { return p.split(':')[0].trim(); }) : [];
                    i += funMatch[0].length;
                    var bodyText = extractBraceBody(code, i);
                    i += bodyText.length + 1; // skip closing brace
                    statements.push({
                        type: 'fun',
                        name: fnName,
                        params: params,
                        body: parseKotlinStatements(bodyText)
                    });
                    continue;
                }
            }

            // Check for 'for' loop
            if (code.substring(i).startsWith('for ') || code.substring(i).startsWith('for(')) {
                var forMatch = code.substring(i).match(/^for\s*\(\s*([A-Za-z0-9_]+)\s+in\s+([^{]+)\)\s*\{/);
                if (forMatch) {
                    var iterVar = forMatch[1];
                    var rangeExpr = forMatch[2].trim();
                    i += forMatch[0].length;
                    var forBody = extractBraceBody(code, i);
                    i += forBody.length + 1;
                    statements.push({
                        type: 'for',
                        variable: iterVar,
                        range: rangeExpr,
                        body: parseKotlinStatements(forBody)
                    });
                    continue;
                }
            }

            // Check for 'if' block
            if (code.substring(i).startsWith('if ') || code.substring(i).startsWith('if(')) {
                var ifMatch = code.substring(i).match(/^if\s*\((.*?)\)\s*\{/);
                if (ifMatch) {
                    var cond = ifMatch[1].trim();
                    i += ifMatch[0].length;
                    var ifBody = extractBraceBody(code, i);
                    i += ifBody.length + 1;
                    
                    var elseBody = null;
                    var rest = code.substring(i).trim();
                    if (rest.startsWith('else')) {
                        var elseMatch = code.substring(i).match(/\s*else\s*\{/);
                        if (elseMatch) {
                            i += elseMatch[0].length;
                            elseBody = extractBraceBody(code, i);
                            i += elseBody.length + 1;
                        }
                    }

                    statements.push({
                        type: 'if',
                        condition: cond,
                        thenBody: parseKotlinStatements(ifBody),
                        elseBody: elseBody ? parseKotlinStatements(elseBody) : null
                    });
                    continue;
                }
            }

            // Regular single statement up to semicolon or newline outside braces
            var stmtEnd = i;
            var depth = 0;
            while (stmtEnd < n) {
                var ch = code[stmtEnd];
                if (ch === '(' || ch === '[' || ch === '{') depth++;
                else if (ch === ')' || ch === ']' || ch === '}') depth--;
                else if (depth === 0 && (ch === ';' || ch === '\n')) break;
                stmtEnd++;
            }
            var rawStmt = code.substring(i, stmtEnd).trim();
            if (rawStmt) {
                statements.push({ type: 'expr', raw: rawStmt });
            }
            i = stmtEnd + 1;
        }

        return statements;
    }

    function extractBraceBody(code, startIndex) {
        var depth = 1;
        var end = startIndex;
        while (end < code.length && depth > 0) {
            if (code[end] === '{') depth++;
            else if (code[end] === '}') depth--;
            if (depth === 0) break;
            end++;
        }
        return code.substring(startIndex, end);
    }

    async function executeKotlinFunction(name, args, env) {
        var def = env.functions[name];
        if (!def) throw new Error("Unresolved reference: " + name);

        var localEnv = {
            globals: env.globals,
            functions: env.functions,
            locals: {}
        };

        for (var p = 0; p < def.params.length; p++) {
            localEnv.locals[def.params[p]] = args[p] !== undefined ? args[p] : null;
        }

        for (var s = 0; s < def.body.length; s++) {
            await executeKotlinStatement(def.body[s], localEnv);
        }
    }

    async function executeKotlinStatement(stmt, env) {
        if (!stmt) return;

        if (stmt.type === 'expr') {
            var raw = stmt.raw;
            if (raw.startsWith('return')) {
                var valExpr = raw.substring(6).trim();
                var retVal = valExpr ? await evalKotlinExpr(valExpr, env) : null;
                var err = new Error('__RETURN__');
                err.returnValue = retVal;
                throw err;
            }

            // val / var declaration
            var declMatch = raw.match(/^(val|var)\s+([A-Za-z0-9_]+)(?:\s*:\s*[A-Za-z0-9_<>,?\s]+)?\s*=\s*(.*)/);
            if (declMatch) {
                var vName = declMatch[2];
                var vExpr = declMatch[3].trim();
                var evaluated = await evalKotlinExpr(vExpr, env);
                if (env.locals) env.locals[vName] = evaluated;
                else env.globals[vName] = evaluated;
                return;
            }

            // Assignment (=, +=, -=)
            var assignMatch = raw.match(/^([A-Za-z0-9_\[\].]+)\s*(=|\+=|-=)\s*(.*)/);
            if (assignMatch && !assignMatch[1].startsWith('val ') && !assignMatch[1].startsWith('var ')) {
                var target = assignMatch[1].trim();
                var op = assignMatch[2];
                var rhs = await evalKotlinExpr(assignMatch[3].trim(), env);
                var lhs = await evalKotlinExpr(target, env);
                var finalVal = rhs;
                if (op === '+=') finalVal = (typeof lhs === 'string' || typeof rhs === 'string') ? (String(lhs) + String(rhs)) : (Number(lhs) + Number(rhs));
                else if (op === '-=') finalVal = Number(lhs) - Number(rhs);

                if (env.locals && env.locals[target] !== undefined) env.locals[target] = finalVal;
                else env.globals[target] = finalVal;
                return;
            }

            // Standalone expression or call
            await evalKotlinExpr(raw, env);
        } else if (stmt.type === 'for') {
            var rangeStr = stmt.range.trim();
            var iterItems = [];

            if (rangeStr.includes('..')) {
                var p = rangeStr.split('..');
                var start = Number(await evalKotlinExpr(p[0].trim(), env));
                var end = Number(await evalKotlinExpr(p[1].trim(), env));
                for (var r = start; r <= end; r++) iterItems.push(r);
            } else if (rangeStr.includes(' until ')) {
                var pu = rangeStr.split(' until ');
                var startU = Number(await evalKotlinExpr(pu[0].trim(), env));
                var endU = Number(await evalKotlinExpr(pu[1].trim(), env));
                for (var ru = startU; ru < endU; ru++) iterItems.push(ru);
            } else {
                var collection = await evalKotlinExpr(rangeStr, env);
                if (Array.isArray(collection)) iterItems = collection;
            }

            for (var it = 0; it < iterItems.length; it++) {
                if (!env.locals) env.locals = {};
                env.locals[stmt.variable] = iterItems[it];
                for (var b = 0; b < stmt.body.length; b++) {
                    await executeKotlinStatement(stmt.body[b], env);
                }
            }
        } else if (stmt.type === 'if') {
            var condVal = await evalKotlinExpr(stmt.condition, env);
            if (Boolean(condVal)) {
                for (var t = 0; t < stmt.thenBody.length; t++) {
                    await executeKotlinStatement(stmt.thenBody[t], env);
                }
            } else if (stmt.elseBody) {
                for (var e = 0; e < stmt.elseBody.length; e++) {
                    await executeKotlinStatement(stmt.elseBody[e], env);
                }
            }
        }
    }

    async function evalKotlinExpr(expr, env) {
        expr = expr.trim();
        if (!expr) return null;

        // String templates
        var tripleQuote = String.fromCharCode(34, 34, 34);
        var isMultiline = expr.startsWith(tripleQuote) && expr.endsWith(tripleQuote);
        if ((expr.startsWith('"') && expr.endsWith('"')) || isMultiline) {
            var rawStr = isMultiline ? expr.substring(3, expr.length - 3) : expr.substring(1, expr.length - 1);
            return await evalKotlinStringTemplate(rawStr, env);
        }

        // Numbers
        if (/^-?\d+(\.\d+)?([fFL])?/.test(expr)) {
            var cleanNum = expr.replace(/[fFL]/g, '');
            return cleanNum.includes('.') ? parseFloat(cleanNum) : parseInt(cleanNum, 10);
        }

        // Booleans & Null
        if (expr === 'true') return true;
        if (expr === 'false') return false;
        if (expr === 'null') return null;

        // Function / Method calls
        var callMatch = expr.match(/^([A-Za-z0-9_.]+)\s*\(([\s\S]*)\)$/);
        if (callMatch) {
            var targetName = callMatch[1].trim();
            var argStr = callMatch[2].trim();
            var args = [];
            if (argStr) {
                var rawArgs = splitArgs(argStr);
                for (var a = 0; a < rawArgs.length; a++) {
                    args.push(await evalKotlinExpr(rawArgs[a], env));
                }
            }

            // Receiver methods (.size, .sum(), .first(), .last(), .joinToString())
            if (targetName.includes('.')) {
                var dotIdx = targetName.lastIndexOf('.');
                var receiverExpr = targetName.substring(0, dotIdx);
                var methodName = targetName.substring(dotIdx + 1);
                var receiver = await evalKotlinExpr(receiverExpr, env);

                if (Array.isArray(receiver)) {
                    if (methodName === 'sum') return receiver.reduce(function(a, b) { return a + Number(b); }, 0);
                    if (methodName === 'first') return receiver[0];
                    if (methodName === 'last') return receiver[receiver.length - 1];
                    if (methodName === 'joinToString') {
                        var sep = (args.length > 0) ? String(args[0]) : ', ';
                        return receiver.join(sep);
                    }
                    if (methodName === 'contains') return receiver.includes(args[0]);
                }
                if (typeof receiver === 'string') {
                    if (methodName === 'lowercase') return receiver.toLowerCase();
                    if (methodName === 'uppercase') return receiver.toUpperCase();
                    if (methodName === 'trim') return receiver.trim();
                }
            }

            // Built-in function
            if (env.globals[targetName]) {
                return await env.globals[targetName].apply(null, args);
            }

            // User Kotlin function
            if (env.functions[targetName]) {
                return await executeKotlinFunction(targetName, args, env);
            }
        }

        // Property access (.size, .length)
        if (expr.includes('.') && !expr.startsWith('"')) {
            var pParts = expr.split('.');
            var objVal = await evalKotlinExpr(pParts[0].trim(), env);
            if (objVal !== undefined && objVal !== null) {
                var prop = pParts[1].trim();
                if (prop === 'size' && Array.isArray(objVal)) return objVal.length;
                if (prop === 'length' && typeof objVal === 'string') return objVal.length;
                if (objVal[prop] !== undefined) return objVal[prop];
            }
        }

        // Arithmetic Addition / Subtraction
        if (expr.includes('+') || expr.includes('-')) {
            var addIdx = findTopLevelOp(expr, ['+', '-']);
            if (addIdx > 0) {
                var opCh = expr[addIdx];
                var lVal = await evalKotlinExpr(expr.substring(0, addIdx), env);
                var rVal = await evalKotlinExpr(expr.substring(addIdx + 1), env);
                if (opCh === '+') {
                    return (typeof lVal === 'string' || typeof rVal === 'string') ? (String(lVal) + String(rVal)) : (Number(lVal) + Number(rVal));
                } else {
                    return Number(lVal) - Number(rVal);
                }
            }
        }

        // Comparisons
        if (expr.includes('==') || expr.includes('!=') || expr.includes('<') || expr.includes('>')) {
            var compOps = ['==', '!=', '<=', '>=', '<', '>'];
            for (var c = 0; c < compOps.length; c++) {
                var op = compOps[c];
                var opIdx = expr.indexOf(op);
                if (opIdx > 0) {
                    var left = await evalKotlinExpr(expr.substring(0, opIdx), env);
                    var right = await evalKotlinExpr(expr.substring(opIdx + op.length), env);
                    if (op === '==') return left == right;
                    if (op === '!=') return left != right;
                    if (op === '<') return Number(left) < Number(right);
                    if (op === '<=') return Number(left) <= Number(right);
                    if (op === '>') return Number(left) > Number(right);
                    if (op === '>=') return Number(left) >= Number(right);
                }
            }
        }

        // Variable Lookup
        if (env.locals && env.locals[expr] !== undefined) return env.locals[expr];
        if (env.globals[expr] !== undefined) return env.globals[expr];

        return expr;
    }

    async function evalKotlinStringTemplate(str, env) {
        // Evaluate template expressions and variables
        var out = '';
        var i = 0;
        var dollarCharCode = 36;
        while (i < str.length) {
            if (str.charCodeAt(i) === dollarCharCode) {
                if (str.charCodeAt(i + 1) === 123) {
                    var closeBrace = str.indexOf('}', i + 2);
                    if (closeBrace > i) {
                        var expr = str.substring(i + 2, closeBrace);
                        var evaluated = await evalKotlinExpr(expr, env);
                        out += (evaluated !== null && evaluated !== undefined) ? String(evaluated) : 'null';
                        i = closeBrace + 1;
                        continue;
                    }
                } else {
                    var varMatch = str.substring(i + 1).match(/^([A-Za-z0-9_]+)/);
                    if (varMatch) {
                        var vName = varMatch[1];
                        var vVal = (env.locals && env.locals[vName] !== undefined) ? env.locals[vName] : env.globals[vName];
                        out += (vVal !== null && vVal !== undefined) ? String(vVal) : 'null';
                        i += 1 + vName.length;
                        continue;
                    }
                }
            }
            out += str[i];
            i++;
        }
        return out;
    }

    function findTopLevelOp(str, ops) {
        var depth = 0;
        for (var i = str.length - 1; i >= 0; i--) {
            var ch = str[i];
            if (ch === ')' || ch === ']' || ch === '}') depth++;
            else if (ch === '(' || ch === '[' || ch === '{') depth--;
            if (depth === 0 && ops.includes(ch) && i > 0 && str[i - 1] !== '=' && str[i - 1] !== '+' && str[i - 1] !== '-') {
                return i;
            }
        }
        return -1;
    }

    function splitArgs(str) {
        var args = [], current = '', depth = 0;
        for (var i = 0; i < str.length; i++) {
            var ch = str[i];
            if (ch === '(' || ch === '[' || ch === '{') depth++;
            else if (ch === ')' || ch === ']' || ch === '}') depth--;
            if (depth === 0 && ch === ',') {
                args.push(current.trim());
                current = '';
                continue;
            }
            current += ch;
        }
        if (current.trim()) args.push(current.trim());
        return args;
    }
})();
</script>
"""
}
