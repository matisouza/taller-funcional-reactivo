package com.example.demo;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

public class DojoReactiveTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable.filter(jugador -> jugador.getAge() > 35)
                .subscribe(System.out::println);
    }


    @Test
    void jugadoresMayoresA35SegunClub(){
        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);

        observable.filter(player -> player.getAge() > 35)
                .distinct()
                .groupBy(Player::getClub)
                .flatMap(groupedFlux -> groupedFlux
                        .collectList()
                        .map(list -> {
                            Map<String, List<Player>> map = new HashMap<>();
                            map.put(groupedFlux.key(), list);
                            return map;
                        }))
                .subscribe(map -> {
                    map.forEach((key, value) -> {
                        System.out.println("\n");
                        System.out.println(key + ": ");
                        value.forEach(System.out::println);
                    });
                });

    }


    @Test
    void mejorJugadorConNacionalidadFrancia(){
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable.filter(player -> player.getNational().equals("France"))
                .reduce((player, player2) -> player.getWinners() > player2.getWinners() ? player : player2)
                .subscribe(System.out::println);
    }

    @Test
    void clubsAgrupadosPorNacionalidad(){
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable.collectMultimap(Player::getNational, Player::getClub)
                .subscribe(map -> {
                    map.forEach((nacionalidad, clubList) -> {
                        System.out.println("\nNacionalidad: " + nacionalidad);
                        System.out.println("Clubs: " + clubList);
                    });
                });
    }

    @Test
    void clubConElMejorJugador(){
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable
                .reduce((player1, player2) -> player1.getWinners() > player2.getWinners() ? player1 : player2)
                .map(Player::getClub)
                .subscribe(System.out::println);
    }

    @Test
    void clubConElMejorJugador2() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable.collectList()
                .map(players -> players.stream()
                        .max(Comparator.comparing(Player::getWinners))
                        .orElse(null))
                .map(Player::getClub)
                .subscribe(System.out::println);

    }

    @Test
    void ElMejorJugadorConOptional() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable
                .collectList()
                .map(players -> players.stream()
                        .max(Comparator.comparing(Player::getWinners))
                        .orElse(null))
                .subscribe(System.out::println);
    }

    @Test
    void ElMejorJugador2() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable
                .reduce((player, player2) -> player.getWinners() > player2.getWinners() ? player : player2)
                .subscribe(mejorJugador -> {
                    if (mejorJugador != null) {
                        System.out.println("Mejor jugador: " + mejorJugador.getName());
                    } else {
                        System.out.println("No se encontró al mejor jugador");
                    }
                });
    }

    @Test
    void mejorJugadorSegunNacionalidad(){
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable
                .collect(Collectors.groupingBy(
                        Player::getNational,
                        Collectors.maxBy(Comparator.comparing(Player::getWinners)))
                )
                .subscribe(map -> {
                    map.forEach((nacionalidad, mejorJugador) -> {
                        System.out.println(nacionalidad + ": " + mejorJugador.get().getName());
                    });
                });
    }

    @Test
    void mejorJugadorSegunNacionalidadReactor() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable
                .groupBy(Player::getNational)
                .flatMap(group -> group
                        .reduce((player1, player2) ->
                                player1.getWinners() > player2.getWinners() ? player1 : player2)
                        .map(Player::getName)
                        .defaultIfEmpty("Matias Souza")
                        .map(mejorJugador ->
                                group.key() + ": " + mejorJugador)
                )
                .subscribe(System.out::println);
    }

    @Test
    void mejores10jugadores() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable
                .sort(Comparator.comparing(Player::getWinners).reversed())
                .take(10)
                .subscribe(System.out::println);
    }
    @Test
    void mejores10jugadoresSegunNacionalidad() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);
        String nacionalidad = "Uruguay";

        observable
                .filter(player -> player.getNational().equals(nacionalidad))
                .groupBy(Player::getNational)
                .flatMap(group -> group
                        .sort(Comparator.comparing(Player::getWinners).reversed())
                        .map(player ->
                                "Nacionalidad: " +
                                    group.key()  + "\n" +
                                        "Nombre: " + player.getName() + "\n" +
                                        " - victorias: " + player.getWinners() + "\n" +
                                        " - Cantidad de partidos: " + player.getGames() + "\n"
                        )
                )
                .take(10)
                .subscribe(System.out::println);
    }
}
