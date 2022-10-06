package net.gloryx.glauncher.util

import org.python.util.PythonInterpreter

inline fun python(block: PythonInterpreter.() -> Unit) = PythonInterpreter().use(block)