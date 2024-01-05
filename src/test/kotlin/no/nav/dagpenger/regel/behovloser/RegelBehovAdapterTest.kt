package no.nav.dagpenger.regel.behovloser

import io.kotest.matchers.shouldBe
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegelBehovAdapterTest {
    private val testrapid = TestRapid()

    init {
        RegelBehovAdapter(testrapid)
    }

    @BeforeEach
    fun reset() {
        testrapid.reset()
    }

    @Test
    fun `Skal motta behov om vurdering av minste inntekt og sende ut behovet på riktig format`() {
        testrapid.sendTestMessage(
            testMelding.trimIndent(),
        )

        testrapid.inspektør.size shouldBe 1
        testrapid.inspektør.message(0).let { message ->
            message["behovId"].asText() shouldBe "673f8b3c-16df-4402-a675-0615fbb38bb2"
            message["fødselsnummer"].asText() shouldBe "12345678910"
            message["beregningsDato"].asText() shouldBe "2021-01-01"
            message["kontekstId"].asText() shouldBe "4c72f811-b27c-406b-881e-b01b58becb6f"
            message["kontekstType"].asText() shouldBe "vilkårsvurdering"
            message["@prosessertAv"].asText() shouldBe "dp-regel-behovloser"
        }
    }

    @Test
    fun `Skal ikke behandle prosesserte meldinger`() {
        //language=JSON
        testrapid.sendTestMessage(
            """
            {
              "@event_name": "behov",
              "@behovId": "673f8b3c-16df-4402-a675-0615fbb38bb2",
              "@behov": [
                "VurderingAvMinsteInntekt"
              ],
              "ident": "12345678910",
              "oppgaveUUID": "4c72f811-b27c-406b-881e-b01b58becb6f",
              "stegUUID": "2abaf132-2244-4444-8ce4-969f822d0c7d",
              "virkningsdato": "2021-01-01",
              "@id": "f468c4da-a4a7-4c67-bb9a-7bffb27222fb",
              "@prosessertAv": "dp-regel-behovloser"
            }
            """.trimIndent(),
        )

        testrapid.inspektør.size shouldBe 0
    }

    private val testMelding = """
            {
              "@event_name": "behov",
              "@behovId": "673f8b3c-16df-4402-a675-0615fbb38bb2",
              "@behov": [
                "VurderingAvMinsteInntekt"
              ],
              "ident": "12345678910",
              "oppgaveUUID": "4c72f811-b27c-406b-881e-b01b58becb6f",
              "stegUUID": "2abaf132-2244-4444-8ce4-969f822d0c7d",
              "virkningsdato": "2021-01-01",
              "@id": "f468c4da-a4a7-4c67-bb9a-7bffb27222fb"
            }
            """
}
