package com.example.demo;


import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class DojoStreamTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35(){
        List<Player> list = CsvUtilFile.getPlayers();
        Set<Player> result = list.stream()
                .filter(jugador -> jugador.getAge() > 35)
                .collect(Collectors.toSet());
        result.forEach(System.out::println);
    }

    @Test
    void jugadoresMayoresA35SegunClub(){
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, List<Player>> result = list.stream()
                .filter(player -> player.getAge() > 35)
                .distinct()
                .collect(Collectors.groupingBy(Player::getClub));

        result.forEach((key, jugadores) -> {
            System.out.println("\n");
            System.out.println(key + ": ");
            jugadores.forEach(System.out::println);
        });

    }

    @Test
    void mejorJugadorConNacionalidadFrancia(){
        List<Player> list = CsvUtilFile.getPlayers();
        Optional<Player> result = list.stream()
                .filter(player -> player.getNational().equals("France"))
                .max(Comparator.comparing(Player::getWinners));

        System.out.println(result.get());
    }


    @Test
    void clubsAgrupadosPorNacionalidad(){
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, List<String>> result = list.stream()
                .collect(Collectors.groupingBy(
                        Player::getNational,
                        Collectors.mapping(Player::getClub, Collectors.toList())
                ));

        result.forEach((nacionalidad, clubList) -> {
            System.out.println("\nNacionalidad: " + nacionalidad);
            System.out.println("Clubs: " + clubList);
        });
    }

    @Test
    void clubConElMejorJugador(){
        List<Player> list = CsvUtilFile.getPlayers();
        Optional<String> result = list.stream()
                .max(Comparator.comparing(Player::getWinners))
                .map(Player::getClub);

        System.out.printf("El club con el mejor jugador es: %s", result.get());
    }

    @Test
    void ElMejorJugador(){
        List<Player> list = CsvUtilFile.getPlayers();
        Optional<Player> result = list.stream()
                .max(Comparator.comparing(Player::getWinners));
        System.out.printf("El mejor jugador es: %s", result.get());
    }

    @Test
    void mejorJugadorSegunNacionalidad(){
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, Optional<Player>> result = list.stream()
                .collect(Collectors.groupingBy(Player::getNational, Collectors.maxBy(Comparator.comparing(Player::getWinners))));

        result.forEach((nacionalidad, jugador) -> System.out.printf("\n%s: %s", nacionalidad, jugador.get().getName()));
    }


}
