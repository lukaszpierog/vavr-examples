package com.lpierog.vavr.examples

import io.github.joke.spockoutputcapture.CapturedOutput
import io.github.joke.spockoutputcapture.OutputCapture
import io.vavr.control.Try
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

import static io.vavr.API.*

class O4_TrySpec extends Specification {
    @OutputCapture CapturedOutput logs

    def "try as a verbose block"() {
        given:
        def result = 0

        when:
        try {
            result = 1 / 0
        } catch (Exception e) {
            result = -1
        }

        then:
        result == -1
    }

    def "try as a functional monad instead of verbose block"() {
        expect:
        def i = 0
        Try(() -> 1 / 0)
        .recover(Exception, -1)
        .get() == -1
    }

    def "handling different scenarios in Try"() {
        given:
        def potentiallyFailingMethod = { Try.failure(new RuntimeException("I failed")) }

        when:
        def output = Try(() -> 1 + 1 + 1)
        .map(sum -> "The result was $sum")
        .onSuccess(result -> println result)
        .flatMap(result -> potentiallyFailingMethod())
        .onFailure(e -> println "Error: $e")
        .andFinally(() -> println "I can execute code despite the failure")

        then:
        output.isFailure()
        logs.lines.containsAll(
                "The result was 3",
                "Error: java.lang.RuntimeException: I failed",
                "I can execute code despite the failure")

    }

    def "try with resources is also supported"() {
        when:
        def result = Try.withResources(() -> Files.lines(Paths.get("src/test/resources/text.txt")))
                .of(lines -> lines.findFirst())
                .map(Optional::get)

        then:
        result.isSuccess()
        result.get() == 'I\'m just a text file'

        when:
        result = result.filter(s -> s.contains("dinosaur"), () -> new RuntimeException("No dinosaurs found"))

        then:
        result.isFailure()
        result.getCause().getMessage() == "No dinosaurs found"

    }
}
