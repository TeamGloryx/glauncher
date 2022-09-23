package net.gloryx.glauncher.util.lang

import androidx.compose.ui.text.intl.Locale
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType

enum class Language {
    RU_RU,
    EN_US;

    val conf: Map<String, String>

    init {
        conf = mutableMapOf<String, String>().also { map ->
            val obj = ConfigFactory.parseResources("lang/${name.lowercase()}.conf").root()
            for ((key, value) in obj) {
                recObj(key, value, map)
            }

            
        }
    }

    private fun recObj(key: String, obj: ConfigValue, map: MutableMap<String, String>) {
        if (obj.valueType() == ConfigValueType.STRING) map[key] = obj.unwrapped() as String
        else {
            for (o in (obj as ConfigObject)) {
                if (o.value.valueType() == ConfigValueType.OBJECT) recObj("$key.${o.key}", o.value, map)
                else if (o.value.valueType() == ConfigValueType.STRING) map["$key.${o.key}"] =
                    o.value.unwrapped() as String
            }
        }
    }

    companion object {
        fun from(locale: Locale) = valueOf(locale.toLanguageTag().uppercase().replace('-', '_'))

        val Default = EN_US
    }
}