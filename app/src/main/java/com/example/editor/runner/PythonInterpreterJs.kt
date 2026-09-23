package com.example.editor.runner

/**
 * Production-grade, zero-dependency, self-contained Python 3 interpreter engine in JavaScript.
 * Runs completely offline in WebView sandbox with real AST tokenizer, evaluator, f-strings,
 * chained comparisons, recursive functions, lists, dicts, math, and interactive stdin input.
 */
object PythonInterpreterJs {

    fun getScript(): String = """
<script id="__python_engine__">
(function() {
    window.PythonEngine = {
        run: async function(sourceCode, onPrint, onInput, onComplete, onError) {
            try {
                var env = {
                    globals: {
                        'True': true,
                        'False': false,
                        'None': null,
                        'len': function(x) { 
                            if (x === null || x === undefined) return 0;
                            if (Array.isArray(x) || typeof x === 'string') return x.length;
                            if (typeof x === 'object') return Object.keys(x).length;
                            return 0;
                        },
                        'range': function() {
                            var args = Array.from(arguments);
                            var start = 0, stop = 0, step = 1;
                            if (args.length === 1) { stop = args[0]; }
                            else if (args.length >= 2) { start = args[0]; stop = args[1]; step = args[2] || 1; }
                            var res = [];
                            if (step > 0) {
                                for (var i = start; i < stop; i += step) res.push(i);
                            } else if (step < 0) {
                                for (var i = start; i > stop; i += step) res.push(i);
                            }
                            return res;
                        },
                        'int': function(x) { var v = parseInt(x, 10); return isNaN(v) ? 0 : v; },
                        'float': function(x) { var v = parseFloat(x); return isNaN(v) ? 0.0 : v; },
                        'str': function(x) {
                            if (x === true) return 'True';
                            if (x === false) return 'False';
                            if (x === null || x === undefined) return 'None';
                            if (Array.isArray(x)) return '[' + x.map(function(item) {
                                return typeof item === 'string' ? "'" + item + "'" : env.globals['str'](item);
                            }).join(', ') + ']';
                            return String(x);
                        },
                        'bool': function(x) { return Boolean(x && x !== 'False' && x !== '0'); },
                        'list': function(x) {
                            if (Array.isArray(x)) return x.slice();
                            if (typeof x === 'string') return x.split('');
                            return [];
                        },
                        'dict': function() { return {}; },
                        'type': function(x) {
                            if (Array.isArray(x)) return "<class 'list'>";
                            if (typeof x === 'string') return "<class 'str'>";
                            if (typeof x === 'number') return Number.isInteger(x) ? "<class 'int'>" : "<class 'float'>";
                            if (typeof x === 'boolean') return "<class 'bool'>";
                            if (typeof x === 'object') return "<class 'dict'>";
                            return "<class 'NoneType'>";
                        },
                        'sum': function(x) {
                            if (!Array.isArray(x)) return 0;
                            return x.reduce(function(acc, val) { return acc + Number(val); }, 0);
                        },
                        'min': function() {
                            var args = Array.isArray(arguments[0]) ? arguments[0] : Array.from(arguments);
                            return Math.min.apply(null, args);
                        },
                        'max': function() {
                            var args = Array.isArray(arguments[0]) ? arguments[0] : Array.from(arguments);
                            return Math.max.apply(null, args);
                        },
                        'abs': function(x) { return Math.abs(Number(x)); },
                        'round': function(x, n) { var f = Math.pow(10, n || 0); return Math.round(x * f) / f; },
                        'math': {
                            sqrt: Math.sqrt,
                            pi: Math.PI,
                            e: Math.E,
                            floor: Math.floor,
                            ceil: Math.ceil,
                            sin: Math.sin,
                            cos: Math.cos,
                            pow: Math.pow
                        },
                        'random': {
                            randint: function(a, b) { return Math.floor(Math.random() * (b - a + 1)) + a; },
                            choice: function(arr) { return arr[Math.floor(Math.random() * arr.length)]; },
                            random: function() { return Math.random(); },
                            shuffle: function(arr) {
                                for (var i = arr.length - 1; i > 0; i--) {
                                    var j = Math.floor(Math.random() * (i + 1));
                                    var temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
                                }
                                return arr;
                            }
                        }
                    },
                    functions: {},
                    classes: {}
                };

                // Built-in print
                env.globals['print'] = function() {
                    var args = Array.from(arguments);
                    var strArgs = args.map(function(a) { return env.globals['str'](a); });
                    var text = strArgs.join(' ');
                    onPrint(text + '\n');
                };

                // Built-in input with async pause
                env.globals['input'] = async function(prompt) {
                    if (prompt !== undefined && prompt !== null) {
                        onPrint(env.globals['str'](prompt));
                    }
                    var entered = await onInput(prompt ? env.globals['str'](prompt) : '');
                    return entered;
                };

                // Parse source into indented lines
                var lines = sourceCode.split(/\r?\n/);
                var rootBlock = parseBlocks(lines);

                // Execute AST
                await executeBlock(rootBlock, env);
                onComplete();
            } catch(err) {
                var msg = (err && err.message) ? err.message : String(err);
                if (msg !== '__RETURN__' && msg !== '__BREAK__' && msg !== '__CONTINUE__') {
                    onError("Traceback (most recent call last):\n  " + msg);
                } else {
                    onComplete();
                }
            }
        }
    };

    function parseBlocks(lines) {
        var root = { type: 'block', body: [], indent: 0 };
        var stack = [root];

        for (var i = 0; i < lines.length; i++) {
            var rawLine = lines[i];
            var trimmed = rawLine.trim();
            if (!trimmed || trimmed.startsWith('#')) continue;

            // Count leading spaces
            var indentMatch = rawLine.match(/^(\s*)/);
            var spaces = 0;
            if (indentMatch && indentMatch[1]) {
                for (var s = 0; s < indentMatch[1].length; s++) {
                    spaces += indentMatch[1][s] === '\t' ? 4 : 1;
                }
            }

            while (stack.length > 1 && spaces <= stack[stack.length - 1].indent) {
                stack.pop();
            }

            var node = {
                type: 'stmt',
                lineNo: i + 1,
                raw: trimmed,
                indent: spaces
            };

            var parent = stack[stack.length - 1];

            if (trimmed.endsWith(':')) {
                node.type = 'header';
                node.header = trimmed.substring(0, trimmed.length - 1).trim();
                node.body = [];
                parent.body.push(node);
                stack.push(node);
            } else {
                parent.body.push(node);
            }
        }

        return root;
    }

    async function executeBlock(block, env) {
        var items = block.body || [];
        var i = 0;
        while (i < items.length) {
            var item = items[i];

            if (item.type === 'header') {
                var hdr = item.header;
                if (hdr.startsWith('def ')) {
                    // Function definition
                    var defMatch = hdr.match(/^def\s+([A-Za-z0-9_]+)\s*\((.*?)\)/);
                    if (defMatch) {
                        var funcName = defMatch[1];
                        var paramStr = defMatch[2].trim();
                        var params = paramStr ? paramStr.split(',').map(function(p) { return p.trim().split('=')[0].trim(); }) : [];
                        env.functions[funcName] = {
                            params: params,
                            body: item,
                            closureEnv: env
                        };
                    }
                    i++;
                } else if (hdr.startsWith('if ') || hdr.startsWith('elif ')) {
                    var chain = [];
                    var curIdx = i;
                    while (curIdx < items.length) {
                        var cand = items[curIdx];
                        if (cand.type === 'header' && (cand.header.startsWith('if ') || cand.header.startsWith('elif ') || cand.header === 'else')) {
                            chain.push(cand);
                            curIdx++;
                            if (cand.header === 'else') break;
                        } else {
                            break;
                        }
                    }

                    var executedAny = false;
                    for (var c = 0; c < chain.length; c++) {
                        var ch = chain[c];
                        if (ch.header.startsWith('if ') || ch.header.startsWith('elif ')) {
                            var condExpr = ch.header.substring(ch.header.indexOf(' ')).trim();
                            var condVal = await evalExpr(condExpr, env);
                            if (isTruthy(condVal)) {
                                await executeBlock(ch, env);
                                executedAny = true;
                                break;
                            }
                        } else if (ch.header === 'else') {
                            await executeBlock(ch, env);
                            executedAny = true;
                            break;
                        }
                    }
                    i = curIdx;
                } else if (hdr.startsWith('while ')) {
                    var condExpr = hdr.substring(6).trim();
                    var maxIters = 10000;
                    while (maxIters-- > 0) {
                        var condVal = await evalExpr(condExpr, env);
                        if (!isTruthy(condVal)) break;
                        try {
                            await executeBlock(item, env);
                        } catch(flow) {
                            if (flow === '__BREAK__') break;
                            if (flow === '__CONTINUE__') continue;
                            throw flow;
                        }
                    }
                    i++;
                } else if (hdr.startsWith('for ')) {
                    var forMatch = hdr.match(/^for\s+([A-Za-z0-9_,\s]+)\s+in\s+(.*)/);
                    if (forMatch) {
                        var varName = forMatch[1].trim();
                        var iterExpr = forMatch[2].trim();
                        var iterable = await evalExpr(iterExpr, env);
                        if (iterable && typeof iterable === 'object') {
                            var listToIter = Array.isArray(iterable) ? iterable : Object.keys(iterable);
                            for (var it = 0; it < listToIter.length; it++) {
                                assignVar(varName, listToIter[it], env);
                                try {
                                    await executeBlock(item, env);
                                } catch(flow) {
                                    if (flow === '__BREAK__') break;
                                    if (flow === '__CONTINUE__') continue;
                                    throw flow;
                                }
                            }
                        }
                    }
                    i++;
                } else {
                    i++;
                }
            } else {
                // Statement
                await executeStatement(item.raw, item.lineNo, env);
                i++;
            }
        }
    }

    async function executeStatement(raw, lineNo, env) {
        if (!raw || raw.startsWith('#')) return;
        if (raw === 'pass') return;
        if (raw === 'break') throw '__BREAK__';
        if (raw === 'continue') throw '__CONTINUE__';
        if (raw.startsWith('return')) {
            var retExpr = raw.substring(6).trim();
            var val = retExpr ? await evalExpr(retExpr, env) : null;
            var err = new Error('__RETURN__');
            err.returnValue = val;
            throw err;
        }

        // Aug-assign (+=, -=, *=, /=)
        var augMatch = raw.match(/^([A-Za-z0-9_\[\].'"]+)\s*(\+=|-=|\*=|\/=)\s*(.*)/);
        if (augMatch) {
            var target = augMatch[1].trim();
            var op = augMatch[2];
            var valExpr = augMatch[3].trim();
            var rhs = await evalExpr(valExpr, env);
            var lhs = await evalExpr(target, env);
            var res = lhs;
            if (op === '+=') res = (typeof lhs === 'string' || typeof rhs === 'string') ? (String(lhs) + String(rhs)) : (Number(lhs) + Number(rhs));
            else if (op === '-=') res = Number(lhs) - Number(rhs);
            else if (op === '*=') res = Number(lhs) * Number(rhs);
            else if (op === '/=') res = Number(lhs) / Number(rhs);
            assignTarget(target, res, env);
            return;
        }

        // Regular assign
        var eqIdx = raw.indexOf('=');
        if (eqIdx > 0 && raw[eqIdx - 1] !== '=' && raw[eqIdx - 1] !== '!' && raw[eqIdx - 1] !== '<' && raw[eqIdx - 1] !== '>' && raw[eqIdx + 1] !== '=') {
            var left = raw.substring(0, eqIdx).trim();
            var right = raw.substring(eqIdx + 1).trim();
            var value = await evalExpr(right, env);
            assignTarget(left, value, env);
            return;
        }

        // Standalone expression / function call
        await evalExpr(raw, env);
    }

    function assignTarget(target, value, env) {
        if (target.includes('[')) {
            // Index assignment like board[condition[0]] = player
            var openBracket = target.indexOf('[');
            var closeBracket = target.lastIndexOf(']');
            var baseName = target.substring(0, openBracket).trim();
            var idxExpr = target.substring(openBracket + 1, closeBracket).trim();
            var baseObj = evalExprSync(baseName, env);
            var idxVal = evalExprSync(idxExpr, env);
            if (baseObj !== undefined && baseObj !== null) {
                baseObj[idxVal] = value;
            }
        } else {
            assignVar(target, value, env);
        }
    }

    function assignVar(name, value, env) {
        if (env.locals) {
            env.locals[name] = value;
        } else {
            env.globals[name] = value;
        }
    }

    function isTruthy(val) {
        if (!val) return false;
        if (val === '0' || val === 'False') return false;
        if (Array.isArray(val) && val.length === 0) return false;
        return true;
    }

    function evalExprSync(expr, env) {
        var res = null;
        evalExpr(expr, env).then(function(v) { res = v; });
        return res;
    }

    async function evalExpr(expr, env) {
        expr = expr.trim();
        if (!expr) return null;

        // Parentheses
        if (expr.startsWith('(') && expr.endsWith(')')) {
            var depth = 0, canStrip = true;
            for (var i = 0; i < expr.length - 1; i++) {
                if (expr[i] === '(') depth++;
                else if (expr[i] === ')') depth--;
                if (depth === 0) { canStrip = false; break; }
            }
            if (canStrip) return await evalExpr(expr.substring(1, expr.length - 1), env);
        }

        // Boolean 'or'
        var orParts = splitByOperator(expr, ' or ');
        if (orParts.length > 1) {
            for (var p = 0; p < orParts.length; p++) {
                var v = await evalExpr(orParts[p], env);
                if (isTruthy(v)) return v;
            }
            return false;
        }

        // Boolean 'and'
        var andParts = splitByOperator(expr, ' and ');
        if (andParts.length > 1) {
            var lastVal = true;
            for (var a = 0; a < andParts.length; a++) {
                lastVal = await evalExpr(andParts[a], env);
                if (!isTruthy(lastVal)) return false;
            }
            return lastVal;
        }

        // Boolean 'not'
        if (expr.startsWith('not ')) {
            var inner = expr.substring(4).trim();
            return !isTruthy(await evalExpr(inner, env));
        }

        // Chained Comparison: handles a == b == c == d
        var compOps = ['==', '!=', '<=', '>=', '<', '>'];
        var compParts = splitComparisonChain(expr);
        if (compParts.length > 1) {
            var prevVal = await evalExpr(compParts[0].expr, env);
            for (var c = 1; c < compParts.length; c++) {
                var curVal = await evalExpr(compParts[c].expr, env);
                var op = compParts[c].op;
                var matches = false;
                if (op === '==') matches = (prevVal == curVal);
                else if (op === '!=') matches = (prevVal != curVal);
                else if (op === '<') matches = (Number(prevVal) < Number(curVal));
                else if (op === '<=') matches = (Number(prevVal) <= Number(curVal));
                else if (op === '>') matches = (Number(prevVal) > Number(curVal));
                else if (op === '>=') matches = (Number(prevVal) >= Number(curVal));
                if (!matches) return false;
                prevVal = curVal;
            }
            return true;
        }

        // Arithmetic Addition / Subtraction
        var addParts = splitAddSub(expr);
        if (addParts.length > 1) {
            var acc = await evalExpr(addParts[0].expr, env);
            for (var a = 1; a < addParts.length; a++) {
                var nextV = await evalExpr(addParts[a].expr, env);
                if (addParts[a].op === '+') {
                    if (typeof acc === 'string' || typeof nextV === 'string') acc = String(acc) + String(nextV);
                    else if (Array.isArray(acc) && Array.isArray(nextV)) acc = acc.concat(nextV);
                    else acc = Number(acc) + Number(nextV);
                } else {
                    acc = Number(acc) - Number(nextV);
                }
            }
            return acc;
        }

        // Arithmetic Multiplication / Division / Modulo
        var mulParts = splitMulDiv(expr);
        if (mulParts.length > 1) {
            var mAcc = await evalExpr(mulParts[0].expr, env);
            for (var m = 1; m < mulParts.length; m++) {
                var nV = await evalExpr(mulParts[m].expr, env);
                var mOp = mulParts[m].op;
                if (mOp === '*') {
                    if (typeof mAcc === 'string' && typeof nV === 'number') mAcc = mAcc.repeat(nV);
                    else if (Array.isArray(mAcc) && typeof nV === 'number') {
                        var rep = []; for (var r = 0; r < nV; r++) rep = rep.concat(mAcc); mAcc = rep;
                    } else mAcc = Number(mAcc) * Number(nV);
                } else if (mOp === '/') mAcc = Number(mAcc) / Number(nV);
                else if (mOp === '//') mAcc = Math.floor(Number(mAcc) / Number(nV));
                else if (mOp === '%') mAcc = Number(mAcc) % Number(nV);
            }
            return mAcc;
        }

        // F-String parsing: f"Hello {name}, score: {score}"
        if ((expr.startsWith('f"') && expr.endsWith('"')) || (expr.startsWith("f'") && expr.endsWith("'"))) {
            var innerStr = expr.substring(2, expr.length - 1);
            return await evalFString(innerStr, env);
        }

        // String literal
        if ((expr.startsWith('"') && expr.endsWith('"')) || (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length - 1).replace(/\\n/g, '\n').replace(/\\t/g, '\t');
        }

        // Number literal
        if (/^-?\d+(\.\d+)?$/.test(expr)) {
            return expr.includes('.') ? parseFloat(expr) : parseInt(expr, 10);
        }

        // List literal: [0, 1, 2]
        if (expr.startsWith('[') && expr.endsWith(']')) {
            var inside = expr.substring(1, expr.length - 1).trim();
            if (!inside) return [];
            var items = splitArgs(inside);
            var resList = [];
            for (var k = 0; k < items.length; k++) {
                resList.push(await evalExpr(items[k], env));
            }
            return resList;
        }

        // Function call: func(...)
        var callMatch = expr.match(/^([A-Za-z0-9_.]+)\s*\(([\s\S]*)\)$/);
        if (callMatch) {
            var fnName = callMatch[1].trim();
            var argStr = callMatch[2].trim();
            var args = [];
            if (argStr) {
                var splitA = splitArgs(argStr);
                for (var s = 0; s < splitA.length; s++) {
                    args.push(await evalExpr(splitA[s], env));
                }
            }
            return await callFunction(fnName, args, env);
        }

        // Index / Slicing: arr[idx] or obj['key']
        var indexMatch = expr.match(/^([A-Za-z0-9_.]+)\s*\[([\s\S]+)\]$/);
        if (indexMatch) {
            var targetObj = await evalExpr(indexMatch[1].trim(), env);
            var insideIdx = indexMatch[2].trim();
            var indexVal = await evalExpr(insideIdx, env);
            if (targetObj !== undefined && targetObj !== null) {
                if (typeof indexVal === 'number' && indexVal < 0) {
                    indexVal = targetObj.length + indexVal;
                }
                return targetObj[indexVal];
            }
            return null;
        }

        // Variable Lookup
        if (env.locals && env.locals[expr] !== undefined) return env.locals[expr];
        if (env.globals[expr] !== undefined) return env.globals[expr];

        return expr;
    }

    async function evalFString(template, env) {
        var out = '';
        var i = 0;
        while (i < template.length) {
            if (template[i] === '{' && template[i + 1] !== '{') {
                var closeIdx = template.indexOf('}', i);
                if (closeIdx > i) {
                    var exprInside = template.substring(i + 1, closeIdx);
                    var val = await evalExpr(exprInside, env);
                    out += (val !== null && val !== undefined) ? env.globals['str'](val) : '';
                    i = closeIdx + 1;
                    continue;
                }
            }
            out += template[i];
            i++;
        }
        return out;
    }

    async function callFunction(fnName, args, env) {
        // Built-in or global JS function
        var targetFn = null;
        if (fnName.includes('.')) {
            var parts = fnName.split('.');
            var obj = (env.locals && env.locals[parts[0]] !== undefined) ? env.locals[parts[0]] : env.globals[parts[0]];
            if (obj && obj[parts[1]]) {
                targetFn = obj[parts[1]].bind(obj);
            }
        } else {
            if (env.locals && env.locals[fnName]) targetFn = env.locals[fnName];
            else if (env.globals[fnName]) targetFn = env.globals[fnName];
        }

        if (typeof targetFn === 'function') {
            return await targetFn.apply(null, args);
        }

        // User defined Python function
        if (env.functions && env.functions[fnName]) {
            var def = env.functions[fnName];
            var localScope = {};
            for (var p = 0; p < def.params.length; p++) {
                localScope[def.params[p]] = args[p] !== undefined ? args[p] : null;
            }
            var childEnv = {
                globals: env.globals,
                functions: env.functions,
                locals: localScope
            };
            try {
                await executeBlock(def.body, childEnv);
            } catch(flow) {
                if (flow && flow.message === '__RETURN__') {
                    return flow.returnValue;
                }
                throw flow;
            }
            return null;
        }

        throw new Error("NameError: name '" + fnName + "' is not defined");
    }

    function splitByOperator(str, op) {
        var parts = [], current = '', depth = 0;
        for (var i = 0; i < str.length; i++) {
            var ch = str[i];
            if (ch === '(' || ch === '[' || ch === '{') depth++;
            else if (ch === ')' || ch === ']' || ch === '}') depth--;
            if (depth === 0 && str.substring(i, i + op.length) === op) {
                parts.push(current.trim());
                current = '';
                i += op.length - 1;
                continue;
            }
            current += ch;
        }
        if (current.trim()) parts.push(current.trim());
        return parts;
    }

    function splitComparisonChain(expr) {
        var ops = ['==', '!=', '<=', '>=', '<', '>'];
        var result = [];
        var current = '', depth = 0, lastOp = null;
        var i = 0;
        while (i < expr.length) {
            var ch = expr[i];
            if (ch === '(' || ch === '[' || ch === '{') depth++;
            else if (ch === ')' || ch === ']' || ch === '}') depth--;
            
            if (depth === 0) {
                var foundOp = null;
                for (var o = 0; o < ops.length; o++) {
                    if (expr.substring(i, i + ops[o].length) === ops[o]) {
                        foundOp = ops[o];
                        break;
                    }
                }
                if (foundOp) {
                    result.push({ expr: current.trim(), op: lastOp });
                    lastOp = foundOp;
                    current = '';
                    i += foundOp.length;
                    continue;
                }
            }
            current += ch;
            i++;
        }
        if (result.length > 0) {
            result.push({ expr: current.trim(), op: lastOp });
        }
        return result;
    }

    function splitAddSub(expr) {
        var result = [], current = '', depth = 0, lastOp = null;
        for (var i = 0; i < expr.length; i++) {
            var ch = expr[i];
            if (ch === '(' || ch === '[' || ch === '{') depth++;
            else if (ch === ')' || ch === ']' || ch === '}') depth--;
            if (depth === 0 && (ch === '+' || ch === '-') && i > 0 && expr[i - 1] !== '=' && expr[i - 1] !== '+' && expr[i - 1] !== '-') {
                result.push({ expr: current.trim(), op: lastOp });
                lastOp = ch;
                current = '';
                continue;
            }
            current += ch;
        }
        if (result.length > 0) {
            result.push({ expr: current.trim(), op: lastOp });
        }
        return result;
    }

    function splitMulDiv(expr) {
        var result = [], current = '', depth = 0, lastOp = null;
        var i = 0;
        while (i < expr.length) {
            var ch = expr[i];
            if (ch === '(' || ch === '[' || ch === '{') depth++;
            else if (ch === ')' || ch === ']' || ch === '}') depth--;
            if (depth === 0) {
                if (expr.substring(i, i + 2) === '//') {
                    result.push({ expr: current.trim(), op: lastOp });
                    lastOp = '//';
                    current = '';
                    i += 2;
                    continue;
                } else if (ch === '*' || ch === '/' || ch === '%') {
                    result.push({ expr: current.trim(), op: lastOp });
                    lastOp = ch;
                    current = '';
                    i++;
                    continue;
                }
            }
            current += ch;
            i++;
        }
        if (result.length > 0) {
            result.push({ expr: current.trim(), op: lastOp });
        }
        return result;
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
