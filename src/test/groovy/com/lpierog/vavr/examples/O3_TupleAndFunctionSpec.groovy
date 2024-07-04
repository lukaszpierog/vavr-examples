package com.lpierog.vavr.examples

import io.vavr.CheckedFunction2
import io.vavr.Function2
import io.vavr.Function3
import spock.lang.Specification

import java.util.function.BiFunction

import static io.vavr.API.*

class O3_TupleAndFunctionSpec extends Specification {

    static int sum(int a, int b) throws Exception {
        return a + b
    }

    static int sumAll(int a, int b, int c) {
        return a + b + c
    }

    BiFunction<Integer, Integer, Integer> sumFunctionJava = O3_TupleAndFunctionSpec::sum
    CheckedFunction2<Integer, Integer, Integer> sumFunction = O3_TupleAndFunctionSpec::sum
    Function3<Integer, Integer, Integer, Integer> sumAllFunction = O3_TupleAndFunctionSpec::sumAll

    def "function can be applied to tuple"() {
        given:
        def tuple = Tuple(1, 2)
        when:
        def result = sumFunction.apply(tuple._1, tuple._2)
        then:
        result == 3
        when:
        result = tuple.apply(sumFunction)
        then:
        result == 3
        when:
        def tuple3 = tuple.append(3)
        sumFunction.apply(tuple3._1(), tuple3._3())
        and:
        result = tuple3.apply(sumAllFunction)
        then:
        result == 6
    }

}
