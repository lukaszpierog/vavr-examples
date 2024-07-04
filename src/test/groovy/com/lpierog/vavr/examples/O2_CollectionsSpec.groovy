package com.lpierog.vavr.examples

import io.vavr.collection.HashMultimap
import io.vavr.collection.Stream
import spock.lang.Specification

import java.util.function.Function
import java.util.stream.Collectors

import static io.vavr.API.*

class O2_CollectionsSpec extends Specification {

    def "java collections api is verbose and not fluent"() {
        given:
        def list = new ArrayList<String>()
        list.add("one")
        list.add("two")
        list.add("three")

        when:
        def result = list.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList())

        then:
        result == ["ONE", "TWO", "THREE"]
    }

    def "vavr collections api is fluent and concise"() {
        given:
        def list = List("one", "two", "three")

        when:
        def result = list.map(String::toUpperCase)

        then:
        result.toJavaList() == ["ONE", "TWO", "THREE"]
    }

    def "stream expansions"() {
        when:
        def binarySequence = Stream.from(0)
                .map(i -> Math.pow(2, i))
                .map(Number::intValue)

        then:
        binarySequence.take(10).toJavaList() == [1, 2, 4, 8, 16, 32, 64, 128, 256, 512]
        //note stream can be reused, only first 10 elements have been evaluated, rest is lazy
        binarySequence.get(10) == 1024

        when:
        def randomDoubles = Stream.continually(Math::random)
                .takeUntil(i -> i < 0.1)
        then:
        randomDoubles.size() < Integer.MAX_VALUE
    }

    //operation set is far superior

    def "sort, drop, take, distinct, filter"() {
        given:
        def list = List(1,2,3,7,1,2,9,8,2,3,2,1,5)

        when:
        def result = list
                .sorted()
                .dropRightWhile(i -> i > 6)
                .filter(i -> i % 2 == 1)
                .distinct()

        then:
        result == List(1, 3, 5)
    }

    def "reject, exists"() {
        when:
        def primes = Stream.rangeClosed(10, 30)
            .reject(i -> Stream.rangeClosed(2L, (long) Math.sqrt(i))
                    .exists(d -> i % d == 0))
        then:
        primes.toJavaList() == [11, 13, 17, 19, 23, 29]
    }

    def "toMap, zip"() {
        given:
        def range = Stream.rangeClosed(0, 5)
        when:
        def numbersToSquares = range
                .toMap(i -> i, i -> i * i)
        then:
        numbersToSquares.toJavaMap() ==
                [0: 0,
                 1: 1,
                 2: 4,
                 3: 9,
                 4: 16,
                 5: 25]

        when:
        def squares = range.map(i -> i * i)
        then:
        numbersToSquares == range.zip(squares).toMap(Function.identity())
    }

    def "groupBy"() {
        when:
        def grouped = Stream.rangeClosed(0, 5).groupBy(i -> i % 2 == 0 ? "even" : "odd")
        //unfortunately groovy takes over with its groupBy method, but you get the idea
        then:
        grouped ==
                ["even": [0, 2, 4],
                 "odd": [1, 3, 5]]
    }

    def "reducing, folding"() {
        when:
        def reducedSum = Stream.rangeClosed(0, 2)
                .reduce((i, j) -> i + j)
        def foldedSum = Stream.rangeClosed(0, 2)
                .fold(0, (i, res) -> i + res)

        then:
        reducedSum == 3
        foldedSum == 3

        when:
        int length = 10
        def fibonacci = Stream.range(0, length)
                .foldLeft(List(0, 1), (res, i) -> res.append(res.takeRight(2).sum().intValue()))

        then:
        fibonacci == List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89)

        when:
        def random = {lower, upper -> (int) (Math.random() * (upper - lower)) + lower}

        def sumsTo20 = Stream.continually(() -> random(1, 10)).take(100)
                .foldLeft(List(), (res, i) -> res.append(i).sum() > 20 ? res : res.append(i))

        then:
        sumsTo20.sum().intValue() == 20
    }

    def "different types of collections"() {
        when:
        def seq = Seq(1, 2, 3)//ordered based on insertion
        def set = Set(1, 2, 3)//unordered
        def sortedSet = SortedSet(3, 2, 1)//sorted based on comparator
        def orderedMap = LinkedMap("a", 1, "b", 2, "c", 3)//ordered based on insertion
        def sortedMap = SortedMap("c", 3, "b", 2, "a", 1)//sorted based on comparator
        def vector = Vector(1, 2, 3)//fast random access
        def multimap = HashMultimap.withSeq().of("a", 1, "a", 2, "b", 3)//multimap

        then:
        noExceptionThrown()
    }
}
