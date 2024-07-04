package com.lpierog.vavr.examples

import io.vavr.API
import spock.lang.Specification

class O1_ImmutabilitySpec extends Specification {

    def "java collections are mutable"() {
        given:
        def list = new ArrayList<String>()
        list.add("Java")

        when:
        list.add("is")
        list.add("mutable")

        then:
        list.size() == 3
    }

    def "vavr collections are immutable and therefore thread safe"() {
        given:
        def list = API.List("Vavr")

        when:
        list.append("is")
        list.append("immutable")

        then:
        list.size() == 1

        when:
        list = list.append("is")
        list = list.append("immutable")

        then:
        list.size() == 3
    }

    def "other vavr constructs are also immutable and thread safe"() {
        given:
        def map = API.Map("key", "value")
        def modifiedMap = map.put("new key", "value")

        expect:
        map != modifiedMap

        when:
        def trySum = API.Try(() -> 1 + 1)
        def tryDivide = trySum.mapTry(result -> result / 0)

        then:
        trySum.isSuccess()
        tryDivide.isFailure()

        when:
        def option = API.Option("Hello world")
        def blank = option.filter(String::isBlank)

        then:
        option.isDefined()
        blank.isEmpty()
    }
}
