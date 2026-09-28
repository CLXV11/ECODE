package com.example.editor.syntax

import java.util.Locale

/**
 * Central registry containing all supported language definitions,
 * their associated vector icon badges, MIME types, syntax patterns,
 * and high-performance heuristic file type detection rules.
 */
object LanguageRegistry {

    val PYTHON = LanguageDefinition(
        id = "python",
        name = "Python",
        extensions = listOf("py", "pyw", "pyi"),
        icon = LanguageIcons.PYTHON,
        mimeTypes = listOf("text/x-python", "application/x-python-code"),
        shebangs = listOf("python", "python3"),
        keywords = setOf(
            "and", "as", "assert", "async", "await", "break", "class", "continue", "def",
            "del", "elif", "else", "except", "finally", "for", "from", "global", "if",
            "import", "in", "is", "lambda", "nonlocal", "not", "or", "pass", "raise",
            "return", "try", "while", "with", "yield", "match", "case"
        ),
        types = setOf("int", "float", "str", "bool", "list", "dict", "set", "tuple", "bytes", "object"),
        builtins = setOf(
            "print", "len", "range", "enumerate", "zip", "map", "filter", "min", "max",
            "sum", "open", "type", "isinstance", "issubclass", "id", "input", "format",
            "repr", "dir", "help", "super", "property"
        ),
        constants = setOf("True", "False", "None", "self", "cls"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"\"\"", "'''", "\"", "'"),
        supportsColonIndent = true
    )

    val JAVASCRIPT = LanguageDefinition(
        id = "javascript",
        name = "JavaScript",
        extensions = listOf("js", "mjs", "cjs"),
        icon = LanguageIcons.JAVASCRIPT,
        mimeTypes = listOf("text/javascript", "application/javascript", "application/x-javascript"),
        shebangs = listOf("node"),
        keywords = setOf(
            "break", "case", "catch", "class", "const", "continue", "debugger", "default",
            "delete", "do", "else", "export", "extends", "finally", "for", "function",
            "if", "import", "in", "instanceof", "new", "return", "super", "switch",
            "this", "throw", "try", "typeof", "var", "void", "while", "with", "yield",
            "let", "await", "async", "of", "from", "as"
        ),
        types = setOf("Array", "Boolean", "Date", "Error", "Function", "Number", "Object", "Promise", "RegExp", "String", "Symbol", "Map", "Set"),
        builtins = setOf("console", "document", "window", "setTimeout", "setInterval", "clearTimeout", "clearInterval", "fetch", "require", "process"),
        constants = setOf("true", "false", "null", "undefined", "NaN", "Infinity"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("`", "\"", "'")
    )

    val TYPESCRIPT = LanguageDefinition(
        id = "typescript",
        name = "TypeScript",
        extensions = listOf("ts", "mts", "cts"),
        icon = LanguageIcons.TYPESCRIPT,
        mimeTypes = listOf("application/x-typescript", "text/typescript"),
        shebangs = listOf("ts-node", "deno", "bun"),
        keywords = JAVASCRIPT.keywords + setOf(
            "type", "interface", "enum", "implements", "namespace", "declare",
            "abstract", "as", "is", "keyof", "readonly", "never", "unknown", "any",
            "public", "private", "protected", "override"
        ),
        types = JAVASCRIPT.types + setOf("string", "number", "boolean", "void", "never", "any", "unknown", "Record", "Partial", "Pick", "Omit"),
        builtins = JAVASCRIPT.builtins,
        constants = JAVASCRIPT.constants,
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("`", "\"", "'")
    )

    val JSX = LanguageDefinition(
        id = "jsx",
        name = "JavaScript React (JSX)",
        extensions = listOf("jsx"),
        icon = LanguageIcons.JSX,
        mimeTypes = listOf("text/jsx"),
        keywords = JAVASCRIPT.keywords,
        types = JAVASCRIPT.types,
        builtins = JAVASCRIPT.builtins + setOf("useState", "useEffect", "useMemo", "useCallback", "useRef", "useContext", "React"),
        constants = JAVASCRIPT.constants,
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("`", "\"", "'"),
        isXmlOrHtml = true
    )

    val TSX = LanguageDefinition(
        id = "tsx",
        name = "TypeScript React (TSX)",
        extensions = listOf("tsx"),
        icon = LanguageIcons.TSX,
        mimeTypes = listOf("text/tsx"),
        keywords = TYPESCRIPT.keywords,
        types = TYPESCRIPT.types + setOf("FC", "ReactNode", "PropsWithChildren", "Component"),
        builtins = TYPESCRIPT.builtins + setOf("useState", "useEffect", "useMemo", "useCallback", "useRef", "useContext", "React"),
        constants = TYPESCRIPT.constants,
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("`", "\"", "'"),
        isXmlOrHtml = true
    )

    val HTML = LanguageDefinition(
        id = "html",
        name = "HTML",
        extensions = listOf("html", "htm"),
        icon = LanguageIcons.HTML,
        mimeTypes = listOf("text/html", "application/xhtml+xml"),
        keywords = setOf(
            "doctype", "html", "head", "title", "meta", "link", "style", "script",
            "body", "header", "footer", "nav", "section", "article", "aside", "h1",
            "h2", "h3", "h4", "h5", "h6", "p", "div", "span", "a", "img", "ul",
            "ol", "li", "table", "tr", "td", "th", "form", "input", "button", "textarea",
            "select", "option", "label", "canvas", "svg", "main", "iframe"
        ),
        lineCommentPrefixes = emptyList(),
        blockCommentStart = "<!--",
        blockCommentEnd = "-->",
        stringDelimiters = listOf("\"", "'"),
        isXmlOrHtml = true
    )

    val CSS = LanguageDefinition(
        id = "css",
        name = "CSS",
        extensions = listOf("css"),
        icon = LanguageIcons.CSS,
        mimeTypes = listOf("text/css"),
        keywords = setOf(
            "@import", "@media", "@keyframes", "@charset", "@supports", "@font-face",
            "display", "position", "top", "right", "bottom", "left", "width", "height",
            "margin", "padding", "border", "background", "color", "font-family",
            "font-size", "font-weight", "flex", "grid", "gap", "align-items", "justify-content",
            "overflow", "opacity", "z-index", "transform", "transition", "animation",
            "important"
        ),
        lineCommentPrefixes = emptyList(),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "'")
    )

    val SCSS = LanguageDefinition(
        id = "scss",
        name = "SCSS",
        extensions = listOf("scss", "sass"),
        icon = LanguageIcons.SCSS,
        mimeTypes = listOf("text/x-scss", "text/x-sass"),
        keywords = CSS.keywords + setOf("@mixin", "@include", "@extend", "@function", "@return", "@if", "@else", "@for", "@each", "@while"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "'")
    )

    val LESS = LanguageDefinition(
        id = "less",
        name = "Less",
        extensions = listOf("less"),
        icon = LanguageIcons.LESS,
        mimeTypes = listOf("text/x-less"),
        keywords = CSS.keywords + setOf("@import", "@plugin"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "'")
    )

    val JSON = LanguageDefinition(
        id = "json",
        name = "JSON",
        extensions = listOf("json"),
        icon = LanguageIcons.JSON,
        mimeTypes = listOf("application/json", "application/ld+json"),
        constants = setOf("true", "false", "null"),
        stringDelimiters = listOf("\""),
        isJson = true
    )

    val XML = LanguageDefinition(
        id = "xml",
        name = "XML",
        extensions = listOf("xml", "plist", "xsd", "wsdl"),
        icon = LanguageIcons.XML,
        mimeTypes = listOf("text/xml", "application/xml"),
        lineCommentPrefixes = emptyList(),
        blockCommentStart = "<!--",
        blockCommentEnd = "-->",
        stringDelimiters = listOf("\"", "'"),
        isXmlOrHtml = true
    )

    val KOTLIN = LanguageDefinition(
        id = "kotlin",
        name = "Kotlin",
        extensions = listOf("kt", "kts"),
        icon = LanguageIcons.KOTLIN,
        mimeTypes = listOf("text/x-kotlin"),
        keywords = setOf(
            "package", "import", "class", "interface", "object", "val", "var", "fun",
            "typealias", "constructor", "init", "this", "super", "is", "as", "in",
            "for", "while", "do", "if", "else", "when", "try", "catch", "finally",
            "throw", "return", "continue", "break", "public", "private", "protected",
            "internal", "enum", "sealed", "annotation", "data", "inline", "noinline",
            "crossinline", "suspend", "tailrec", "operator", "infix", "const", "lateinit",
            "vararg", "companion", "open", "final", "abstract", "override", "by"
        ),
        types = setOf(
            "Int", "Long", "Short", "Byte", "Float", "Double", "Boolean", "Char", "String",
            "Unit", "Nothing", "Any", "Array", "List", "Set", "Map", "MutableList", "MutableSet", "MutableMap"
        ),
        constants = setOf("true", "false", "null"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"\"\"", "\"")
    )

    val JAVA = LanguageDefinition(
        id = "java",
        name = "Java",
        extensions = listOf("java"),
        icon = LanguageIcons.JAVA,
        mimeTypes = listOf("text/x-java-source"),
        keywords = setOf(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new", "package",
            "private", "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient",
            "try", "void", "volatile", "while", "record", "sealed", "permits"
        ),
        types = setOf(
            "String", "Object", "Class", "System", "Integer", "Long", "Double", "Float",
            "Boolean", "List", "ArrayList", "Map", "HashMap", "Set", "HashSet"
        ),
        constants = setOf("true", "false", "null"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"")
    )

    val C = LanguageDefinition(
        id = "c",
        name = "C",
        extensions = listOf("c", "h"),
        icon = LanguageIcons.C,
        mimeTypes = listOf("text/x-c", "text/x-csrc"),
        keywords = setOf(
            "auto", "break", "case", "char", "const", "continue", "default", "do",
            "double", "else", "enum", "extern", "float", "for", "goto", "if",
            "inline", "int", "long", "register", "restrict", "return", "short",
            "signed", "sizeof", "static", "struct", "switch", "typedef", "union",
            "unsigned", "void", "volatile", "while"
        ),
        types = setOf("int", "char", "float", "double", "void", "size_t", "uint8_t", "uint16_t", "uint32_t", "uint64_t"),
        constants = setOf("NULL", "true", "false"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "'")
    )

    val CPP = LanguageDefinition(
        id = "cpp",
        name = "C++",
        extensions = listOf("cpp", "cxx", "cc", "hpp", "hxx", "hh"),
        icon = LanguageIcons.CPP,
        mimeTypes = listOf("text/x-c++src", "text/x-c++hdr"),
        keywords = C.keywords + setOf(
            "alignas", "alignof", "asm", "bool", "catch", "class", "concept", "consteval",
            "constexpr", "constinit", "decltype", "delete", "dynamic_cast", "explicit",
            "export", "friend", "mutable", "namespace", "new", "noexcept", "nullptr",
            "operator", "override", "private", "protected", "public", "reinterpret_cast",
            "requires", "static_assert", "static_cast", "template", "this", "thread_local",
            "throw", "try", "typeid", "typename", "using", "virtual"
        ),
        types = C.types + setOf("std::string", "std::vector", "std::map", "std::unique_ptr", "std::shared_ptr"),
        constants = setOf("nullptr", "true", "false"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "'")
    )

    val CSHARP = LanguageDefinition(
        id = "csharp",
        name = "C#",
        extensions = listOf("cs"),
        icon = LanguageIcons.CSHARP,
        mimeTypes = listOf("text/x-csharp"),
        keywords = setOf(
            "abstract", "as", "base", "bool", "break", "byte", "case", "catch", "char",
            "checked", "class", "const", "continue", "decimal", "default", "delegate",
            "do", "double", "else", "enum", "event", "explicit", "extern", "false",
            "finally", "fixed", "float", "for", "foreach", "goto", "if", "implicit",
            "in", "int", "interface", "internal", "is", "lock", "long", "namespace",
            "new", "null", "object", "operator", "out", "override", "params", "private",
            "protected", "public", "readonly", "ref", "return", "sbyte", "sealed",
            "short", "sizeof", "stackalloc", "static", "string", "struct", "switch",
            "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked",
            "unsafe", "ushort", "using", "virtual", "void", "volatile", "while", "async", "await", "var"
        ),
        types = setOf("String", "Int32", "Int64", "Boolean", "Task", "List", "Dictionary", "Action", "Func"),
        constants = setOf("true", "false", "null"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("$\"", "@\"", "\"")
    )

    val GO = LanguageDefinition(
        id = "go",
        name = "Go",
        extensions = listOf("go"),
        icon = LanguageIcons.GO,
        mimeTypes = listOf("text/x-gosrc"),
        keywords = setOf(
            "break", "case", "chan", "const", "continue", "default", "defer", "else",
            "fallthrough", "for", "func", "go", "goto", "if", "import", "interface",
            "map", "package", "range", "return", "select", "struct", "switch", "type", "var"
        ),
        types = setOf(
            "bool", "string", "int", "int8", "int16", "int32", "int64", "uint", "uint8",
            "uint16", "uint32", "uint64", "uintptr", "byte", "rune", "float32", "float64",
            "complex64", "complex128", "error"
        ),
        builtins = setOf("append", "cap", "close", "complex", "copy", "delete", "imag", "len", "make", "new", "panic", "print", "println", "real", "recover"),
        constants = setOf("true", "false", "iota", "nil"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("`", "\"")
    )

    val RUST = LanguageDefinition(
        id = "rust",
        name = "Rust",
        extensions = listOf("rs"),
        icon = LanguageIcons.RUST,
        mimeTypes = listOf("text/rust", "text/x-rust"),
        keywords = setOf(
            "as", "async", "await", "break", "const", "continue", "crate", "dyn", "else",
            "enum", "extern", "false", "fn", "for", "if", "impl", "in", "let", "loop",
            "match", "mod", "move", "mut", "pub", "ref", "return", "self", "Self",
            "static", "struct", "super", "trait", "true", "type", "unsafe", "use",
            "where", "while"
        ),
        types = setOf(
            "i8", "i16", "i32", "i64", "i128", "isize", "u8", "u16", "u32", "u64", "u128",
            "usize", "f32", "f64", "bool", "char", "str", "String", "Vec", "Option", "Result", "Box"
        ),
        constants = setOf("true", "false", "Some", "None", "Ok", "Err"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "'")
    )

    val SWIFT = LanguageDefinition(
        id = "swift",
        name = "Swift",
        extensions = listOf("swift"),
        icon = LanguageIcons.SWIFT,
        mimeTypes = listOf("text/x-swift"),
        keywords = setOf(
            "associatedtype", "class", "deinit", "enum", "extension", "fileprivate",
            "func", "import", "init", "inout", "internal", "let", "open", "operator",
            "private", "protocol", "public", "rethrows", "static", "struct", "subscript",
            "typealias", "var", "break", "case", "continue", "default", "defer", "do",
            "else", "fallthrough", "for", "guard", "if", "in", "repeat", "return",
            "switch", "where", "while", "as", "is", "try", "throw", "catch", "async", "await"
        ),
        types = setOf("Int", "Double", "Float", "String", "Bool", "Array", "Dictionary", "Set", "Optional"),
        constants = setOf("true", "false", "nil"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"\"\"", "\"")
    )

    val DART = LanguageDefinition(
        id = "dart",
        name = "Dart",
        extensions = listOf("dart"),
        icon = LanguageIcons.DART,
        mimeTypes = listOf("application/dart", "text/x-dart"),
        keywords = setOf(
            "abstract", "as", "assert", "async", "await", "break", "case", "catch",
            "class", "const", "continue", "covariant", "default", "deferred", "do",
            "dynamic", "else", "enum", "export", "extends", "extension", "external",
            "factory", "false", "final", "finally", "for", "Function", "get", "hide",
            "if", "implements", "import", "in", "interface", "is", "late", "library",
            "mixin", "new", "null", "on", "operator", "part", "required", "rethrow",
            "return", "set", "show", "static", "super", "switch", "sync", "this",
            "throw", "true", "try", "typedef", "var", "void", "while", "with", "yield"
        ),
        types = setOf("int", "double", "String", "bool", "List", "Map", "Set", "Future", "Stream", "Widget"),
        constants = setOf("true", "false", "null"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"\"\"", "'''", "\"", "'")
    )

    val PHP = LanguageDefinition(
        id = "php",
        name = "PHP",
        extensions = listOf("php", "phtml", "php3", "php4", "php5"),
        icon = LanguageIcons.PHP,
        mimeTypes = listOf("text/x-php", "application/x-httpd-php"),
        shebangs = listOf("php"),
        keywords = setOf(
            "abstract", "and", "array", "as", "break", "callable", "case", "catch",
            "class", "clone", "const", "continue", "declare", "default", "die", "do",
            "echo", "else", "elseif", "empty", "enddeclare", "endfor", "endforeach",
            "endif", "endswitch", "endwhile", "eval", "exit", "extends", "final",
            "finally", "fn", "for", "foreach", "function", "global", "goto", "if",
            "implements", "include", "include_once", "instanceof", "insteadof", "interface",
            "isset", "list", "match", "namespace", "new", "or", "print", "private",
            "protected", "public", "readonly", "require", "require_once", "return",
            "static", "switch", "throw", "trait", "try", "unset", "use", "var", "while", "xor", "yield"
        ),
        constants = setOf("true", "false", "null", "__LINE__", "__FILE__", "__DIR__"),
        lineCommentPrefixes = listOf("//", "#"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "'")
    )

    val RUBY = LanguageDefinition(
        id = "ruby",
        name = "Ruby",
        extensions = listOf("rb", "rake", "gemspec", "ru"),
        icon = LanguageIcons.RUBY,
        mimeTypes = listOf("text/x-ruby", "application/x-ruby"),
        shebangs = listOf("ruby"),
        keywords = setOf(
            "alias", "and", "begin", "break", "case", "class", "def", "defined?",
            "do", "else", "elsif", "end", "ensure", "false", "for", "if", "in",
            "module", "next", "nil", "not", "or", "redo", "rescue", "retry",
            "return", "self", "super", "then", "true", "undef", "unless", "until",
            "when", "while", "yield"
        ),
        constants = setOf("true", "false", "nil"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val LUA = LanguageDefinition(
        id = "lua",
        name = "Lua",
        extensions = listOf("lua"),
        icon = LanguageIcons.LUA,
        mimeTypes = listOf("text/x-lua"),
        shebangs = listOf("lua"),
        keywords = setOf(
            "and", "break", "do", "else", "elsif", "end", "false", "for", "function",
            "goto", "if", "in", "local", "nil", "not", "or", "repeat", "return",
            "then", "true", "until", "while"
        ),
        constants = setOf("true", "false", "nil"),
        lineCommentPrefixes = listOf("--"),
        blockCommentStart = "--[[",
        blockCommentEnd = "]]",
        stringDelimiters = listOf("\"", "'")
    )

    val R = LanguageDefinition(
        id = "r",
        name = "R",
        extensions = listOf("r", "R"),
        icon = LanguageIcons.R,
        mimeTypes = listOf("text/x-r"),
        shebangs = listOf("Rscript"),
        keywords = setOf("if", "else", "repeat", "while", "function", "for", "in", "next", "break", "TRUE", "FALSE", "NULL", "Inf", "NaN", "NA"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val PERL = LanguageDefinition(
        id = "perl",
        name = "Perl",
        extensions = listOf("pl", "pm", "t"),
        icon = LanguageIcons.PERL,
        mimeTypes = listOf("text/x-perl"),
        shebangs = listOf("perl"),
        keywords = setOf(
            "my", "our", "local", "sub", "use", "require", "package", "if", "unless",
            "else", "elsif", "while", "until", "for", "foreach", "do", "return", "die", "warn", "print"
        ),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val SHELL = LanguageDefinition(
        id = "shell",
        name = "Shell / Bash",
        extensions = listOf("sh", "bash", "zsh"),
        icon = LanguageIcons.SHELL,
        mimeTypes = listOf("application/x-sh", "text/x-shellscript"),
        shebangs = listOf("bash", "sh", "zsh"),
        keywords = setOf(
            "if", "then", "else", "elif", "fi", "case", "esac", "for", "select",
            "while", "until", "do", "done", "in", "function", "time", "export",
            "local", "readonly", "echo", "printf", "read", "cd", "pwd", "exit", "return", "source"
        ),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val POWERSHELL = LanguageDefinition(
        id = "powershell",
        name = "PowerShell",
        extensions = listOf("ps1", "psm1", "psd1"),
        icon = LanguageIcons.POWERSHELL,
        mimeTypes = listOf("application/x-powershell"),
        keywords = setOf("if", "else", "elseif", "switch", "foreach", "for", "do", "while", "until", "break", "continue", "return", "function", "filter", "param", "try", "catch", "finally", "throw", "trap"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val SQL = LanguageDefinition(
        id = "sql",
        name = "SQL",
        extensions = listOf("sql", "dsql", "sqlite", "mysql", "pgsql"),
        icon = LanguageIcons.SQL,
        mimeTypes = listOf("application/sql", "text/x-sql"),
        keywords = setOf(
            "SELECT", "FROM", "WHERE", "INSERT", "INTO", "UPDATE", "DELETE", "CREATE",
            "TABLE", "DROP", "ALTER", "ADD", "CONSTRAINT", "PRIMARY", "KEY", "FOREIGN",
            "REFERENCES", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER", "ON",
            "GROUP", "BY", "ORDER", "HAVING", "LIMIT", "OFFSET", "UNION", "ALL",
            "DISTINCT", "AS", "AND", "OR", "NOT", "IN", "IS", "NULL", "LIKE", "BETWEEN",
            "EXISTS", "CASE", "WHEN", "THEN", "ELSE", "END", "INDEX", "VIEW", "TRIGGER",
            "select", "from", "where", "insert", "into", "update", "delete", "create",
            "table", "drop", "alter", "join", "on", "group", "order", "by", "having"
        ),
        types = setOf(
            "INT", "INTEGER", "BIGINT", "SMALLINT", "VARCHAR", "TEXT", "CHAR", "BOOLEAN",
            "TIMESTAMP", "DATE", "TIME", "FLOAT", "DOUBLE", "DECIMAL", "NUMERIC", "BLOB"
        ),
        constants = setOf("TRUE", "FALSE", "NULL", "true", "false", "null"),
        lineCommentPrefixes = listOf("--", "#"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("'", "\"")
    )

    val YAML = LanguageDefinition(
        id = "yaml",
        name = "YAML",
        extensions = listOf("yaml", "yml"),
        icon = LanguageIcons.YAML,
        mimeTypes = listOf("text/yaml", "application/x-yaml"),
        constants = setOf("true", "false", "null", "yes", "no"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val TOML = LanguageDefinition(
        id = "toml",
        name = "TOML",
        extensions = listOf("toml"),
        icon = LanguageIcons.TOML,
        mimeTypes = listOf("application/toml"),
        constants = setOf("true", "false"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"\"\"", "'''", "\"", "'")
    )

    val MARKDOWN = LanguageDefinition(
        id = "markdown",
        name = "Markdown",
        extensions = listOf("md", "markdown", "mdown", "mkd"),
        icon = LanguageIcons.MARKDOWN,
        mimeTypes = listOf("text/markdown", "text/x-markdown"),
        lineCommentPrefixes = emptyList(),
        stringDelimiters = listOf("`")
    )

    val OBJECTIVE_C = LanguageDefinition(
        id = "objectivec",
        name = "Objective-C",
        extensions = listOf("m"),
        icon = LanguageIcons.OBJECTIVE_C,
        mimeTypes = listOf("text/x-objectivec"),
        keywords = C.keywords + setOf("@interface", "@implementation", "@end", "@property", "@synthesize", "@dynamic", "self", "super", "id", "instancetype", "nil", "YES", "NO"),
        types = C.types + setOf("NSString", "NSArray", "NSDictionary", "NSNumber", "NSObject"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("@\"", "\"")
    )

    val OBJECTIVE_CPP = LanguageDefinition(
        id = "objectivecpp",
        name = "Objective-C++",
        extensions = listOf("mm"),
        icon = LanguageIcons.OBJECTIVE_CPP,
        mimeTypes = listOf("text/x-objectivec++"),
        keywords = CPP.keywords + OBJECTIVE_C.keywords,
        types = CPP.types + OBJECTIVE_C.types,
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("@\"", "\"")
    )

    val SCALA = LanguageDefinition(
        id = "scala",
        name = "Scala",
        extensions = listOf("scala", "sc"),
        icon = LanguageIcons.SCALA,
        mimeTypes = listOf("text/x-scala"),
        keywords = setOf("abstract", "case", "catch", "class", "def", "do", "else", "extends", "false", "final", "finally", "for", "forSome", "if", "implicit", "import", "lazy", "match", "new", "null", "object", "override", "package", "private", "protected", "return", "sealed", "super", "this", "throw", "trait", "try", "true", "type", "val", "var", "while", "with", "yield"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"\"\"", "\"")
    )

    val GROOVY = LanguageDefinition(
        id = "groovy",
        name = "Groovy",
        extensions = listOf("groovy", "gvy", "gy", "gsh"),
        icon = LanguageIcons.GROOVY,
        mimeTypes = listOf("text/x-groovy"),
        keywords = JAVA.keywords + setOf("as", "assert", "def", "in", "trait"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"\"\"", "'''", "\"", "'")
    )

    val HASKELL = LanguageDefinition(
        id = "haskell",
        name = "Haskell",
        extensions = listOf("hs", "lhs"),
        icon = LanguageIcons.HASKELL,
        mimeTypes = listOf("text/x-haskell"),
        keywords = setOf("case", "class", "data", "default", "deriving", "do", "else", "foreign", "if", "import", "in", "infix", "infixl", "infixr", "instance", "let", "module", "newtype", "of", "then", "type", "where"),
        lineCommentPrefixes = listOf("--"),
        blockCommentStart = "{-",
        blockCommentEnd = "-}",
        stringDelimiters = listOf("\"")
    )

    val ELIXIR = LanguageDefinition(
        id = "elixir",
        name = "Elixir",
        extensions = listOf("ex", "exs"),
        icon = LanguageIcons.ELIXIR,
        mimeTypes = listOf("text/x-elixir"),
        keywords = setOf("def", "defmodule", "defp", "defmacro", "defguard", "defstruct", "defprotocol", "defimpl", "do", "end", "if", "unless", "cond", "case", "fn", "quote", "unquote", "try", "catch", "rescue", "after", "receive", "alias", "require", "import", "use"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"\"\"", "\"")
    )

    val ERLANG = LanguageDefinition(
        id = "erlang",
        name = "Erlang",
        extensions = listOf("erl", "hrl"),
        icon = LanguageIcons.ERLANG,
        mimeTypes = listOf("text/x-erlang"),
        keywords = setOf("after", "and", "andalso", "band", "begin", "bnot", "bor", "bsl", "bsr", "bxor", "case", "catch", "cond", "div", "end", "fun", "if", "let", "not", "of", "or", "orelse", "receive", "rem", "try", "when", "xor"),
        lineCommentPrefixes = listOf("%"),
        stringDelimiters = listOf("\"")
    )

    val ASSEMBLY = LanguageDefinition(
        id = "assembly",
        name = "Assembly",
        extensions = listOf("asm", "s", "nasm"),
        icon = LanguageIcons.ASSEMBLY,
        mimeTypes = listOf("text/x-asm"),
        keywords = setOf("mov", "push", "pop", "add", "sub", "inc", "dec", "jmp", "je", "jne", "jz", "jnz", "cmp", "call", "ret", "nop", "int", "lea", "xor", "and", "or", "section", "global", "extern"),
        lineCommentPrefixes = listOf(";", "//"),
        stringDelimiters = listOf("\"", "'")
    )

    val MAKEFILE = LanguageDefinition(
        id = "makefile",
        name = "Makefile",
        extensions = listOf("mk", "make"),
        icon = LanguageIcons.MAKEFILE,
        mimeTypes = listOf("text/x-makefile"),
        keywords = setOf("all", "clean", "install", "test", "ifeq", "ifneq", "else", "endif", "define", "endef", "include"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val DOCKERFILE = LanguageDefinition(
        id = "dockerfile",
        name = "Dockerfile",
        extensions = listOf("dockerfile"),
        icon = LanguageIcons.DOCKERFILE,
        mimeTypes = listOf("text/x-dockerfile"),
        keywords = setOf("FROM", "RUN", "CMD", "LABEL", "EXPOSE", "ENV", "ADD", "COPY", "ENTRYPOINT", "VOLUME", "USER", "WORKDIR", "ARG", "ONBUILD", "STOPSIGNAL", "HEALTHCHECK", "SHELL"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val GRADLE = LanguageDefinition(
        id = "gradle",
        name = "Gradle",
        extensions = listOf("gradle"),
        icon = LanguageIcons.GRADLE,
        mimeTypes = listOf("text/x-gradle"),
        keywords = GROOVY.keywords + setOf("plugins", "android", "dependencies", "implementation", "api", "testImplementation", "repositories", "defaultConfig", "buildTypes", "compileSdk", "targetSdk", "minSdk"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"\"\"", "'''", "\"", "'")
    )

    val INI = LanguageDefinition(
        id = "ini",
        name = "INI Configuration",
        extensions = listOf("ini", "cfg", "conf"),
        icon = LanguageIcons.INI,
        mimeTypes = listOf("text/x-ini"),
        lineCommentPrefixes = listOf(";", "#"),
        stringDelimiters = listOf("\"", "'")
    )

    val PROPERTIES = LanguageDefinition(
        id = "properties",
        name = "Properties",
        extensions = listOf("properties", "env"),
        icon = LanguageIcons.PROPERTIES,
        mimeTypes = listOf("text/x-java-properties"),
        lineCommentPrefixes = listOf("#", "!"),
        stringDelimiters = listOf("\"", "'")
    )

    val CSV = LanguageDefinition(
        id = "csv",
        name = "CSV",
        extensions = listOf("csv", "tsv"),
        icon = LanguageIcons.CSV,
        mimeTypes = listOf("text/csv", "text/tab-separated-values"),
        stringDelimiters = listOf("\"")
    )

    val GRAPHQL = LanguageDefinition(
        id = "graphql",
        name = "GraphQL",
        extensions = listOf("graphql", "gql"),
        icon = LanguageIcons.GRAPHQL,
        mimeTypes = listOf("application/graphql"),
        keywords = setOf("query", "mutation", "subscription", "fragment", "on", "type", "interface", "union", "enum", "scalar", "input", "implements", "directive", "schema"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"\"\"", "\"")
    )

    val VUE = LanguageDefinition(
        id = "vue",
        name = "Vue.js",
        extensions = listOf("vue"),
        icon = LanguageIcons.VUE,
        mimeTypes = listOf("text/x-vue"),
        keywords = JAVASCRIPT.keywords + setOf("template", "script", "style", "scoped", "setup", "ref", "reactive", "computed", "watch"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "<!--",
        blockCommentEnd = "-->",
        stringDelimiters = listOf("`", "\"", "'"),
        isXmlOrHtml = true
    )

    val SVELTE = LanguageDefinition(
        id = "svelte",
        name = "Svelte",
        extensions = listOf("svelte"),
        icon = LanguageIcons.SVELTE,
        mimeTypes = listOf("application/x-svelte"),
        keywords = JAVASCRIPT.keywords + setOf("script", "style", "let", "export", "onMount", "onDestroy"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "<!--",
        blockCommentEnd = "-->",
        stringDelimiters = listOf("`", "\"", "'"),
        isXmlOrHtml = true
    )

    val ASTRO = LanguageDefinition(
        id = "astro",
        name = "Astro",
        extensions = listOf("astro"),
        icon = LanguageIcons.ASTRO,
        mimeTypes = listOf("text/x-astro"),
        keywords = JAVASCRIPT.keywords,
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "<!--",
        blockCommentEnd = "-->",
        stringDelimiters = listOf("`", "\"", "'"),
        isXmlOrHtml = true
    )

    val JULIA = LanguageDefinition(
        id = "julia",
        name = "Julia",
        extensions = listOf("jl"),
        icon = LanguageIcons.JULIA,
        mimeTypes = listOf("text/x-julia"),
        keywords = setOf("function", "end", "if", "else", "elseif", "while", "for", "in", "begin", "return", "break", "continue", "macro", "module", "using", "import", "export", "struct", "mutable"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"\"\"", "\"")
    )

    val LATEX = LanguageDefinition(
        id = "latex",
        name = "LaTeX",
        extensions = listOf("tex", "latex", "sty", "cls"),
        icon = LanguageIcons.LATEX,
        mimeTypes = listOf("application/x-latex", "text/x-tex"),
        keywords = setOf("\\documentclass", "\\begin", "\\end", "\\usepackage", "\\section", "\\subsection", "\\textbf", "\\textit", "\\item"),
        lineCommentPrefixes = listOf("%")
    )

    val GIT = LanguageDefinition(
        id = "git",
        name = "Git Configuration",
        extensions = listOf("git", "gitignore", "gitattributes", "gitmodules"),
        icon = LanguageIcons.GIT,
        mimeTypes = listOf("text/plain"),
        lineCommentPrefixes = listOf("#")
    )

    val CLOJURE = LanguageDefinition(
        id = "clojure",
        name = "Clojure",
        extensions = listOf("clj", "cljs", "cljc", "edn"),
        icon = LanguageIcons.CLOJURE,
        mimeTypes = listOf("application/clojure", "text/x-clojure"),
        keywords = setOf(
            "def", "defn", "defn-", "defmacro", "defmulti", "defmethod", "defprotocol",
            "defrecord", "deftype", "let", "fn", "if", "when", "when-not", "when-let",
            "if-let", "cond", "case", "loop", "recur", "quote", "var", "do", "throw",
            "try", "catch", "finally", "ns", "require", "use", "import", "in-ns"
        ),
        types = setOf("String", "Long", "Double", "Boolean", "Object", "Class"),
        builtins = setOf(
            "println", "prn", "print", "str", "count", "first", "rest", "cons", "conj",
            "map", "filter", "reduce", "assoc", "dissoc", "get", "update", "vector",
            "hash-map", "hash-set", "concat", "into", "empty?", "nil?", "true?", "false?"
        ),
        constants = setOf("nil", "true", "false"),
        lineCommentPrefixes = listOf(";"),
        stringDelimiters = listOf("\"")
    )

    val ZIG = LanguageDefinition(
        id = "zig",
        name = "Zig",
        extensions = listOf("zig", "zon"),
        icon = LanguageIcons.ZIG,
        mimeTypes = listOf("text/x-zig"),
        keywords = setOf(
            "fn", "const", "var", "pub", "struct", "enum", "union", "error", "while",
            "for", "if", "else", "switch", "return", "defer", "errdefer", "try",
            "catch", "orelse", "unreachable", "test", "inline", "comptime", "export",
            "extern", "usingnamespace", "break", "continue", "asm", "volatile", "threadlocal"
        ),
        types = setOf(
            "u8", "u16", "u32", "u64", "u128", "usize", "i8", "i16", "i32", "i64", "i128",
            "isize", "f16", "f32", "f64", "f128", "bool", "void", "noreturn", "type",
            "anyerror", "anyopaque"
        ),
        constants = setOf("true", "false", "null", "undefined"),
        lineCommentPrefixes = listOf("//"),
        stringDelimiters = listOf("\"", "\\\\")
    )

    val NIM = LanguageDefinition(
        id = "nim",
        name = "Nim",
        extensions = listOf("nim", "nims", "nimble"),
        icon = LanguageIcons.NIM,
        mimeTypes = listOf("text/x-nim"),
        keywords = setOf(
            "proc", "func", "method", "iterator", "macro", "template", "var", "let",
            "const", "type", "if", "elif", "else", "while", "for", "in", "case", "of",
            "return", "discard", "import", "export", "from", "include", "try", "except",
            "finally", "raise", "defer", "block", "break", "continue", "when", "asm"
        ),
        types = setOf(
            "int", "int8", "int16", "int32", "int64", "uint", "uint8", "uint16", "uint32",
            "uint64", "float", "float32", "float64", "string", "bool", "char", "seq",
            "array", "tuple", "object", "ref", "ptr", "auto"
        ),
        constants = setOf("true", "false", "nil"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"\"\"", "\""),
        supportsColonIndent = true
    )

    val CRYSTAL = LanguageDefinition(
        id = "crystal",
        name = "Crystal",
        extensions = listOf("cr"),
        icon = LanguageIcons.CRYSTAL,
        mimeTypes = listOf("text/x-crystal"),
        keywords = setOf(
            "def", "end", "class", "module", "struct", "enum", "if", "elsif", "else",
            "unless", "while", "until", "loop", "break", "next", "return", "yield",
            "rescue", "ensure", "begin", "require", "include", "extend", "abstract",
            "alias", "annotation", "asm", "case", "when", "macro", "pointerof", "sizeof",
            "instance_sizeof", "typeof", "lib", "out", "fun", "uninitialized", "select"
        ),
        types = setOf(
            "Int8", "Int16", "Int32", "Int64", "UInt8", "UInt16", "UInt32", "UInt64",
            "Float32", "Float64", "String", "Bool", "Char", "Array", "Hash", "Nil",
            "Symbol", "Tuple", "NamedTuple"
        ),
        constants = setOf("true", "false", "nil", "self"),
        lineCommentPrefixes = listOf("#"),
        stringDelimiters = listOf("\"", "'")
    )

    val SOLIDITY = LanguageDefinition(
        id = "solidity",
        name = "Solidity",
        extensions = listOf("sol"),
        icon = LanguageIcons.SOLIDITY,
        mimeTypes = listOf("text/x-solidity"),
        keywords = setOf(
            "contract", "interface", "library", "is", "function", "modifier", "event",
            "error", "struct", "enum", "mapping", "public", "private", "internal",
            "external", "pure", "view", "payable", "returns", "return", "require",
            "revert", "assert", "emit", "if", "else", "for", "while", "do", "break",
            "continue", "try", "catch", "new", "delete", "override", "virtual",
            "memory", "storage", "calldata", "constructor", "fallback", "receive"
        ),
        types = setOf(
            "address", "bool", "string", "bytes", "bytes1", "bytes4", "bytes32",
            "uint", "uint8", "uint16", "uint32", "uint64", "uint128", "uint256",
            "int", "int8", "int16", "int32", "int64", "int128", "int256"
        ),
        constants = setOf("msg", "block", "tx", "this", "true", "false", "abi"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "'")
    )

    val FSHARP = LanguageDefinition(
        id = "fsharp",
        name = "F#",
        extensions = listOf("fs", "fsi", "fsx"),
        icon = LanguageIcons.FSHARP,
        mimeTypes = listOf("text/x-fsharp"),
        keywords = setOf(
            "let", "rec", "mutable", "type", "module", "open", "match", "with",
            "if", "then", "else", "elif", "function", "fun", "member", "val",
            "new", "inherit", "interface", "abstract", "default", "override",
            "async", "task", "try", "finally", "use", "do", "yield", "return",
            "namespace", "static", "inline"
        ),
        types = setOf("int", "int64", "float", "string", "bool", "unit", "list", "array", "option", "seq", "Result"),
        constants = setOf("true", "false", "null"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "(*",
        blockCommentEnd = "*)",
        stringDelimiters = listOf("\"\"\"", "\"")
    )

    val OCAML = LanguageDefinition(
        id = "ocaml",
        name = "OCaml",
        extensions = listOf("ml", "mli"),
        icon = LanguageIcons.OCAML,
        mimeTypes = listOf("text/x-ocaml"),
        keywords = setOf(
            "let", "rec", "in", "type", "module", "open", "match", "with", "if",
            "then", "else", "function", "fun", "val", "sig", "struct", "end",
            "try", "exception", "begin", "and", "as", "class", "constraint",
            "done", "downto", "for", "inherit", "initializer", "lazy", "method",
            "mutable", "new", "object", "of", "private", "raise", "to", "virtual", "while"
        ),
        types = setOf("int", "float", "string", "bool", "char", "unit", "list", "array", "option", "ref"),
        constants = setOf("true", "false"),
        lineCommentPrefixes = emptyList(),
        blockCommentStart = "(*",
        blockCommentEnd = "*)",
        stringDelimiters = listOf("\"", "'")
    )

    val FORTRAN = LanguageDefinition(
        id = "fortran",
        name = "Fortran",
        extensions = listOf("f90", "f95", "f03", "f08", "f", "for"),
        icon = LanguageIcons.FORTRAN,
        mimeTypes = listOf("text/x-fortran"),
        keywords = setOf(
            "program", "end", "subroutine", "function", "module", "use", "implicit",
            "none", "contains", "call", "return", "if", "then", "else", "else if",
            "endif", "do", "while", "enddo", "select", "case", "allocate", "deallocate",
            "where", "elsewhere", "endwhere", "cycle", "exit"
        ),
        types = setOf("integer", "real", "double precision", "complex", "logical", "character", "type"),
        constants = setOf(".true.", ".false."),
        lineCommentPrefixes = listOf("!", "c", "C", "*"),
        stringDelimiters = listOf("\"", "'")
    )

    val COBOL = LanguageDefinition(
        id = "cobol",
        name = "COBOL",
        extensions = listOf("cbl", "cob", "cpy"),
        icon = LanguageIcons.COBOL,
        mimeTypes = listOf("text/x-cobol"),
        keywords = setOf(
            "identification", "division", "program-id", "environment", "configuration",
            "section", "input-output", "file-control", "data", "file", "working-storage",
            "linkage", "procedure", "perform", "thru", "through", "until", "varying",
            "if", "else", "end-if", "move", "to", "display", "stop", "run", "pic",
            "picture", "value", "values", "comp", "comp-3", "evaluate", "when",
            "end-evaluate", "compute", "add", "subtract", "multiply", "divide"
        ),
        types = setOf("pic", "picture", "comp", "comp-3", "usage"),
        lineCommentPrefixes = listOf("*>"),
        stringDelimiters = listOf("\"", "'")
    )

    val D_LANG = LanguageDefinition(
        id = "d",
        name = "D",
        extensions = listOf("d", "di"),
        icon = LanguageIcons.D_LANG,
        mimeTypes = listOf("text/x-d"),
        keywords = setOf(
            "auto", "bool", "byte", "cast", "catch", "class", "const", "continue",
            "debug", "default", "delegate", "delete", "deprecated", "do", "double",
            "else", "enum", "export", "extern", "false", "final", "finally", "float",
            "for", "foreach", "function", "goto", "if", "immutable", "import", "in",
            "inout", "int", "interface", "invariant", "is", "mixin", "module", "new",
            "nothrow", "null", "out", "override", "package", "pragma", "private",
            "protected", "public", "pure", "real", "ref", "return", "scope", "shared",
            "short", "static", "struct", "super", "switch", "synchronized", "template",
            "this", "throw", "true", "try", "typeid", "typeof", "ubyte", "uint",
            "ulong", "union", "unittest", "ushort", "version", "void", "while", "with"
        ),
        types = setOf(
            "int", "uint", "long", "ulong", "short", "ushort", "byte", "ubyte",
            "float", "double", "real", "char", "wchar", "dchar", "bool", "void", "string"
        ),
        constants = setOf("true", "false", "null"),
        lineCommentPrefixes = listOf("//"),
        blockCommentStart = "/*",
        blockCommentEnd = "*/",
        stringDelimiters = listOf("\"", "`")
    )

    val BALLERINA = LanguageDefinition(
        id = "ballerina",
        name = "Ballerina",
        extensions = listOf("bal"),
        icon = LanguageIcons.BALLERINA,
        mimeTypes = listOf("text/x-ballerina"),
        keywords = setOf(
            "function", "returns", "public", "private", "isolated", "remote",
            "resource", "service", "client", "type", "record", "error", "if",
            "else", "while", "foreach", "in", "match", "check", "checkpanic",
            "trap", "return", "var", "const", "final", "import", "worker",
            "fork", "wait", "panic", "retry", "transaction", "commit", "rollback"
        ),
        types = setOf("int", "float", "decimal", "string", "boolean", "xml", "json", "byte", "table", "map", "any", "anydata"),
        constants = setOf("true", "false", "null"),
        lineCommentPrefixes = listOf("//"),
        stringDelimiters = listOf("\"")
    )

    val PLAIN_TEXT = LanguageDefinition.PLAIN_TEXT

    /**
     * All registered languages, sorted alphabetically for the language selector.
     */
    val ALL_LANGUAGES: List<LanguageDefinition> = listOf(
        PYTHON, JAVASCRIPT, TYPESCRIPT, JSX, TSX, HTML, CSS, SCSS, LESS,
        JSON, XML, KOTLIN, JAVA, C, CPP, CSHARP, GO, RUST, SWIFT, DART,
        PHP, RUBY, LUA, R, PERL, SHELL, POWERSHELL, SQL, YAML, TOML,
        MARKDOWN, OBJECTIVE_C, OBJECTIVE_CPP, SCALA, GROOVY, HASKELL,
        ELIXIR, ERLANG, CLOJURE, ZIG, NIM, CRYSTAL, SOLIDITY, FSHARP,
        OCAML, FORTRAN, COBOL, D_LANG, BALLERINA, ASSEMBLY, MAKEFILE,
        DOCKERFILE, GRADLE, INI, PROPERTIES, CSV, GRAPHQL, VUE, SVELTE,
        ASTRO, JULIA, LATEX, GIT, PLAIN_TEXT
    )

    private val extensionMap: Map<String, LanguageDefinition> by lazy {
        val map = mutableMapOf<String, LanguageDefinition>()
        for (lang in ALL_LANGUAGES) {
            for (ext in lang.extensions) {
                map[ext.lowercase(Locale.ROOT)] = lang
            }
        }
        map
    }

    private val mimeMap: Map<String, LanguageDefinition> by lazy {
        val map = mutableMapOf<String, LanguageDefinition>()
        for (lang in ALL_LANGUAGES) {
            for (m in lang.mimeTypes) {
                map[m.lowercase(Locale.ROOT)] = lang
            }
        }
        map
    }

    /**
     * Finds language by exact ID.
     */
    fun findById(id: String): LanguageDefinition {
        return ALL_LANGUAGES.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: PLAIN_TEXT
    }

    /**
     * Fast multi-stage language and icon detector.
     * Order of evaluation:
     * 1. File Extension
     * 2. Known Exact File Names (Dockerfile, Makefile, .gitignore, etc.)
     * 3. MIME Type (if provided)
     * 4. Shebang in file header
     * 5. Content Keyword Heuristics
     * 6. Plain Text fallback
     */
    fun detectLanguage(
        fileName: String,
        contentSample: String = "",
        mimeType: String? = null
    ): LanguageDefinition {
        val trimmedName = fileName.trim()

        // 1. File Extension detection (highest priority & instant)
        val dotIndex = trimmedName.lastIndexOf('.')
        if (dotIndex != -1 && dotIndex < trimmedName.length - 1) {
            val ext = trimmedName.substring(dotIndex + 1).lowercase(Locale.ROOT)
            extensionMap[ext]?.let { return it }
        }

        // 2. Exact filename matching
        val lowerName = trimmedName.lowercase(Locale.ROOT)
        when (lowerName) {
            "dockerfile", "dockerfile.dev", "dockerfile.prod" -> return DOCKERFILE
            "makefile", "gnumakefile" -> return MAKEFILE
            ".bashrc", ".zshrc", ".profile", ".bash_profile" -> return SHELL
            ".env", ".env.local", ".env.example", ".env.production" -> return PROPERTIES
            ".gitignore", ".gitattributes", ".gitmodules" -> return GIT
            ".dockerignore" -> return DOCKERFILE
            "gemfile", "rakefile" -> return RUBY
            "cmakelists.txt" -> return MAKEFILE
            "build.zig" -> return ZIG
            "project.clj", "deps.edn" -> return CLOJURE
            "ballerina.toml" -> return BALLERINA
        }

        // 3. MIME Type detection
        if (!mimeType.isNullOrBlank()) {
            val cleanMime = mimeType.trim().lowercase(Locale.ROOT)
            mimeMap[cleanMime]?.let { return it }
        }

        // 4. Shebang & 5. Content heuristics
        if (contentSample.isNotBlank()) {
            val sample = if (contentSample.length > 2048) contentSample.substring(0, 2048) else contentSample
            val firstLine = sample.lineSequence().firstOrNull()?.trim() ?: ""

            // 4. Shebang check
            if (firstLine.startsWith("#!")) {
                for (lang in ALL_LANGUAGES) {
                    for (sh in lang.shebangs) {
                        if (firstLine.contains(sh, ignoreCase = true)) {
                            return lang
                        }
                    }
                }
            }

            // 5. Content heuristics
            if (sample.contains("fun main(") || sample.contains("fun main ") || sample.contains("package com.")) {
                return KOTLIN
            }
            if (sample.contains("package main") || sample.contains("import (\n\t\"fmt\"")) {
                return GO
            }
            if (sample.contains("public static void main(String[]") || sample.contains("import java.")) {
                return JAVA
            }
            if (sample.contains("using System;") || sample.contains("namespace ")) {
                return CSHARP
            }
            if (sample.contains("fn main()") || sample.contains("use std::")) {
                return RUST
            }
            if (sample.contains("<?php")) {
                return PHP
            }
            if (sample.startsWith("<!DOCTYPE html", ignoreCase = true) || sample.contains("<html", ignoreCase = true)) {
                return HTML
            }
            if (sample.startsWith("<?xml", ignoreCase = true)) {
                return XML
            }
            if ((sample.startsWith("{") && sample.endsWith("}")) || (sample.startsWith("[") && sample.endsWith("]"))) {
                if (sample.contains("\":") || sample.contains("\": ")) {
                    return JSON
                }
            }
            if (sample.contains("def ") && sample.contains(":\n")) {
                return PYTHON
            }
            if (sample.contains("SELECT ") && sample.contains(" FROM ", ignoreCase = true)) {
                return SQL
            }
            if (sample.startsWith("---") && sample.contains(": ")) {
                return YAML
            }
            if (sample.contains("pragma solidity")) {
                return SOLIDITY
            }
            if (sample.contains("(ns ") || sample.contains("(defn ")) {
                return CLOJURE
            }
            if (sample.contains("pub fn main(") || sample.contains("@import(\"std\")")) {
                return ZIG
            }
        }

        // 6. Plain Text fallback
        return PLAIN_TEXT
    }
}
