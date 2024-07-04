package com.lpierog.vavr.examples

import io.vavr.API
import io.vavr.control.Option
import io.vavr.control.Try
import spock.lang.Specification

class O5_ConversionsSpec extends Specification {

    def "monads can be easily mapped between one another"() {
        given:
        def option = API.Option("Hello world")
        when:
        def toTry = option.toTry()
        then:
        toTry.isSuccess()
        toTry.get() == "Hello world"
        when:
        def either = toTry.toEither()
        then:
        either.isRight()
        either.get() == "Hello world"
        when:
        def validation = either.toValidation()
        then:
        validation.isValid()
        validation.get() == "Hello world"

        when:
        option = Option.none()
        and:
        toTry = option.toTry()
        then:
        toTry.isFailure()
        def exception = toTry.getCause()
        when:
        either = toTry.toEither()
        then:
        either.isLeft()
        exception == either.getLeft()
        when:
        validation = either.toValidation()
        then:
        validation.isInvalid()
        exception == validation.getError()
    }

    def "collections can be mapped to one another"() {
        given:
        def list = API.List("one", "two", "three")
        when:
        def toStream = list.toStream()
        then:
        toStream.size() == 3
        toStream.get(1) == "two"
        when:
        def toSet = list.toSet()
        then:
        toSet.size() == 3
        toSet.contains("two")
        when:
        def toMap = list.toMap(s -> s, s -> s.length())
        then:
        toMap.size() == 3
        toMap.get("two") == Option.some(3)
    }

    def "vavr collections can be mapped to java collections"() {
        given:
        def list = API.List("one", "two", "three")
        when:
        def toJavaList = list.toJavaList()
        then:
        toJavaList == ["one", "two", "three"]
        when:
        def toJavaSet = list.toJavaSet()
        then:
        toJavaSet == ["one", "two", "three"] as Set
    }

    def "vavr collections can be mapped to monads"() {
        given:
        def list = API.List("one", "two", "three")
        when:
        def toTry = list.toTry()
        def toTryList = Try.success(list)
        then:
        toTry.isSuccess()
        toTry.get() == list.head()
        toTryList.isSuccess()
        toTryList.get() == list
        when:
        def toOption = list.toOption()
        def toOptionList = Option.some(list)
        then:
        toOption.isDefined()
        toOption == list.headOption()
        toOptionList.isDefined()
        toOptionList.get() == list
    }

}
