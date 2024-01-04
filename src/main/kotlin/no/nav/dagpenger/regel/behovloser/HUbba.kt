package no.nav.dagpenger.regel.behovloser

import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

private val logger = KotlinLogging.logger { }
private val sikkerLogg = KotlinLogging.logger("tjenestekall")

class HUbba(rapidsConnection: RapidsConnection) : River.PacketListener {
    companion object {
        val rapidFilter: River.() -> Unit = {
            validate { it.requireValue("@event_name", "behov") }
            validate { it.requireAll("@behov", listOf("VurderingAvMinsteInntekt")) }
            validate { it.requireKey("ident", "virkningsdato", "@behovId", "oppgaveUUID") }
            validate { it.rejectKey("@løsning") }
        }
    }

    init {
        River(rapidsConnection).apply(rapidFilter).register(this)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
    ) {
        val behovId = packet["@behovId"].asText()
        val fødselsnummer = packet["ident"].asText()
        val oppgaveUUID = packet["oppgaveUUID"].asText()
        withLoggingContext("behovId" to behovId) {
            sikkerLogg.info("Mottok behov for vurdering av minsteinntekt: ${packet.toJson()}")

            packet["behovId"] = behovId
            packet["fødselsnummer"] = fødselsnummer
            packet["beregningsDato"] = packet["virkningsdato"].asText()
            packet["kontekstId"] = oppgaveUUID
            packet["kontekstType"] = "saksbehandling"

            context.publish(packet.toJson())
        }

    }
}
