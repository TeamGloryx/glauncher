import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.javafx.application.PlatformImpl
import net.gloryx.glauncher.ui.Main
import net.gloryx.glauncher.util.Static

fun main() = application(exitProcessOnExit = true) {
    val fin = object : PlatformImpl.FinishListener {
        override fun idle(implicitExit: Boolean) {}
        override fun exitCalled() {}
    }
    PlatformImpl.addListener(fin)

    Window(title = "Gloryx Launcher", onCloseRequest = {
        PlatformImpl.removeListener(fin)
        exitApplication()
    }) {
        Static.window = window
        Main()
    }
}
