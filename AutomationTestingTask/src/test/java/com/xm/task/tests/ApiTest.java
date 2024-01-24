package com.xm.task.tests;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ApiTest {

	@Test
	public void automationTaskApi() {

		// 1.Search for a person with the name Vader.
		Response response = RestAssured.get("https://swapi.dev/api/people/?search=vader");
		List<String> films = response.jsonPath().getList("results.films[0]");
		String vaderUrl = response.jsonPath().getString("results.url[0]");

		// 2.Using previous response (1) find which film that Darth Vader joined has the
		// less planets.
		int minPlanets = Integer.MAX_VALUE;
		String filmUrl = "";
		for (String film : films) {
			Response filmResponse = RestAssured.get(film);
			int planets = filmResponse.jsonPath().getList("planets").size();
			if (planets < minPlanets) {
				minPlanets = planets;
				filmUrl = film;
			}
		}

		// 3.Using previous responses (1) & (2) verify if Vader's starship is on film
		// from response (2).
		Response filmResponse = RestAssured.get(filmUrl);
		List<String> starships = filmResponse.jsonPath().getList("starships");
		boolean present = false;
		for (String starship : starships) {
			Response starshipResponse = RestAssured.get(starship);
			List<String> pilots = starshipResponse.jsonPath().getList("pilots");
			if (pilots.contains(vaderUrl)) {
				present = true;
				break;
			}
		}
		Assert.assertTrue(present);

		// 4.Find the oldest person ever played in all Star Wars films with less than 10
		// requests.
		int oldAge = Integer.MIN_VALUE;
		String oldestPerson = "";
		for (int i = 1; i <= 9; i++) {
			Response peopleResponse = RestAssured.get("https://swapi.dev/api/people/?page=" + i);
			List<String> people = peopleResponse.jsonPath().getList("results.url");
			for (String person : people) {
				Response personResponse = RestAssured.get(person);
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String birthYear = personResponse.jsonPath().getString("birth_year");
				if (!birthYear.equals("unknown")) {
					int age = 2024 - Integer.parseInt(birthYear.replaceAll("[^0-9]", ""));
					if (age > oldAge) {
						oldAge = age;
						oldestPerson = personResponse.jsonPath().getString("name");
					}
				}
			}
		}

		// Print the information about the oldest person
		System.out.println("Oldest person ever played in all Star Wars films:");
		System.out.println("Name: " + oldestPerson);
		System.out.println("Age: " + oldAge);

		// Step 5: Create contract (JSON schema validation) test for /people API
		RestAssured.given().header("Content-Type", "application/json").when().get("https://swapi.dev/api/people/")
				.then().assertThat().body(matchesJsonSchemaInClasspath("peopleschema.json"));
	}
}