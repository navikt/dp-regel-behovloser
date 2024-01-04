package no.nav.dagpenger.regel.behovloser

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding

internal object Configuration {
    const val APP_NAME = "dp-regel-behovloser"

    private val defaultProperties =
        ConfigurationMap(
            mapOf(
                "RAPID_APP_NAME" to APP_NAME,
                "KAFKA_CONSUMER_GROUP_ID" to "dp-regel-behovloser-v1",
                "KAFKA_RAPID_TOPIC" to "teamdagpenger.regel.v1",
                "KAFKA_EXTRA_TOPIC" to "teamdagpenger.rapid.v1",
                "KAFKA_RESET_POLICY" to "latest",
            ),
        )
    val properties =
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding defaultProperties

    val config: Map<String, String> =
        properties.list().reversed().fold(emptyMap()) { map, pair ->
            map + pair.second
        }


//    val azureAdClient by lazy {
//        val azureAdConfig = OAuth2Config.AzureAd(properties)
//        CachedOauth2Client(
//            tokenEndpointUrl = azureAdConfig.tokenEndpointUrl,
//            authType = azureAdConfig.clientSecret(),
//        )
//    }
}
