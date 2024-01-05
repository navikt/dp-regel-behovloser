package no.nav.dagpenger.regel.behovloser

import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

private val logger = KotlinLogging.logger { }
private val sikkerLogg = KotlinLogging.logger("tjenestekall")

class RegelBehovAdapter(rapidsConnection: RapidsConnection) : River.PacketListener {
    companion object {
        val rapidFilter: River.() -> Unit = {
            validate { it.requireValue("@event_name", "behov") }
            validate { it.requireAll("@behov", listOf("VurderingAvMinsteInntekt")) }
            validate { it.requireKey("ident", "virkningsdato", "@behovId", "oppgaveUUID") }
            validate { it.rejectValue("@prosessertAv", "dp-regel-behovloser") }
        }
    }

    init {
        River(rapidsConnection).apply(rapidFilter).register(this)
    }

    override fun onError(
        problems: MessageProblems,
        context: MessageContext,
    ) {
        super.onError(problems, context)
    }

    override fun onSevere(
        error: MessageProblems.MessageException,
        context: MessageContext,
    ) {
        super.onSevere(error, context)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
    ) {
        try {
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
                packet["@prosessertAv"] = "dp-regel-behovloser"

                context.publish(packet.toJson())
            }
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    }
}
