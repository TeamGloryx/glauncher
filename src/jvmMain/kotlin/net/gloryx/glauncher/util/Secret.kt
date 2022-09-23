package net.gloryx.glauncher.util

import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.net.URLEncoder

object Secret {
//    val hcl = HoconConfigurationLoader.builder().buildAndLoadString(
//        javaClass.getResourceAsStream("SECRET.conf")!!.use { it.reader().use { r -> r.readText() } })

    val clientId: String = "8fc924ce-fa22-47ed-bc4d-7cc0075e2d49".replace("-", "") //hcl.getString("clientId")
    val secret: String = URLEncoder.encode("QDd8Q~e5YYknXmT55jZwrch_XE3AEWHTWRBL7bpJ", Charsets.UTF_8) //hcl.getString("secret")

    const val FILE = "./secret.json"
}