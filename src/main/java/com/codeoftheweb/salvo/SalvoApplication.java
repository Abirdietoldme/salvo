package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.*;
import com.codeoftheweb.salvo.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.Arrays;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {

		SpringApplication.run(SalvoApplication.class, args);

		//Mensaje de fin de compilación OK
		System.out.println("OK Polilla");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepos,
									  GameRepository gameRepos,
									  GamePlayerRepository gpRepos,
									  ShipRepository shipRepository,
									  ScoreRepository scoreRepository,
									  SalvoRepository salvoRepository){
		return (args) -> {


			Player p1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player p2 = new Player("c.obrian@ctu.gov",passwordEncoder().encode("42"));
			Player p3 = new Player("kim_bauer@gmail.com",passwordEncoder().encode("kb"));
			Player p4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));

			playerRepos.save (p1);
			playerRepos.save (p2);
			playerRepos.save (p3);
			playerRepos.save (p4);

			Date date  = new Date();
			Date date2 = Date.from(date.toInstant().plusSeconds(3600));
			Date date3 = Date.from(date2.toInstant().plusSeconds(3600));
			Date date4 = Date.from(date3.toInstant().plusSeconds(3600));
			Date date5 = Date.from(date4.toInstant().plusSeconds(3600));
			Date date6 = Date.from(date5.toInstant().plusSeconds(3600));
			Date date7 = Date.from(date6.toInstant().plusSeconds(3600));
			Date date8 = Date.from(date7.toInstant().plusSeconds(3600));

			Game g1 = new Game(date);
			Game g2 = new Game(date2);
			Game g3 = new Game(date3);
			Game g4 = new Game(date4);
			Game g5 = new Game(date5);
			Game g6 = new Game(date6);
			Game g7 = new Game(date7);
			Game g8 = new Game(date8);

			gameRepos.save(g1);
			gameRepos.save(g2);
			gameRepos.save(g3);
			gameRepos.save(g4);
			gameRepos.save(g5);
			gameRepos.save(g6);
			gameRepos.save(g7);
			gameRepos.save(g8);

			//game1
			GamePlayer gp1 = new GamePlayer(g1,p1);
			GamePlayer gp2 = new GamePlayer(g1,p2);

			//game2
			GamePlayer gp3 = new GamePlayer(g2,p1);
			GamePlayer gp4 = new GamePlayer(g2,p2);

			//game3
			GamePlayer gp5 = new GamePlayer(g3,p2);
			GamePlayer gp6 = new GamePlayer(g3,p4);

			//game4
			GamePlayer gp7 = new GamePlayer(g4,p2);
			GamePlayer gp8 = new GamePlayer(g4,p1);

			//game5
			GamePlayer gp9 = new GamePlayer(g5,p4);
			GamePlayer gp10 = new GamePlayer(g5,p1);

			//game6
			GamePlayer gp11 = new GamePlayer(g6,p3);

			//game7
			GamePlayer gp12 = new GamePlayer(g7,p4);

			//game8
			GamePlayer gp13 = new GamePlayer(g8,p3);
			GamePlayer gp14 = new GamePlayer(g8,p4);


			gpRepos.save(gp1);
			gpRepos.save(gp2);
			gpRepos.save(gp3);
			gpRepos.save(gp4);
			gpRepos.save(gp5);
			gpRepos.save(gp6);
			gpRepos.save(gp7);
			gpRepos.save(gp8);
			gpRepos.save(gp9);
			gpRepos.save(gp10);
			gpRepos.save(gp11);
			gpRepos.save(gp12);
			gpRepos.save(gp13);
			gpRepos.save(gp14);

			String shipTypeSubmarine = "submarine";
			String shipTypeDestroyer = "destroyer";
			String shipTypePatrolBoat = "patrol_boat";
			String shipTypeBattleship = "battleship";
			String shipTypeCarrier = "carrier";


			Ship ship1 = new Ship(shipTypeDestroyer, Arrays.asList("H2","H3","H4"), gp1);
			Ship ship2 = new Ship(shipTypeSubmarine, Arrays.asList("E1","F1","G1"), gp1);
			Ship ship3 = new Ship(shipTypePatrolBoat,  Arrays.asList("B4","B5"), gp1);
			Ship ship4 = new Ship(shipTypeDestroyer, Arrays.asList("B5","C5","D5"), gp2);
			Ship ship5 = new Ship(shipTypePatrolBoat, Arrays.asList("F1","F2"), gp2);
			Ship ship6 = new Ship(shipTypeDestroyer, Arrays.asList("B5","C5","D5"), gp3);
			Ship ship7 = new Ship(shipTypePatrolBoat, Arrays.asList("C6","C7"), gp3);
			Ship ship8 = new Ship(shipTypeSubmarine, Arrays.asList("A2","A3","A4"), gp4);
			Ship ship9 = new Ship(shipTypePatrolBoat, Arrays.asList("G6","H6"), gp4);
			Ship ship10 = new Ship(shipTypeDestroyer, Arrays.asList("B5","C5","D5"), gp5);
			Ship ship11 = new Ship(shipTypePatrolBoat, Arrays.asList("C6","C7"), gp5);
			Ship ship12 = new Ship(shipTypeSubmarine, Arrays.asList("A2","A3","A4"), gp6);
			Ship ship13 = new Ship(shipTypePatrolBoat, Arrays.asList("G6","H6"), gp6);
			Ship ship14 = new Ship(shipTypeDestroyer, Arrays.asList("B5","C5","D5"), gp7);
			Ship ship15 = new Ship(shipTypePatrolBoat, Arrays.asList("C6","C7"), gp7);
			Ship ship16 = new Ship(shipTypeSubmarine, Arrays.asList("A2","A3","A4"), gp8);
			Ship ship17 = new Ship(shipTypePatrolBoat, Arrays.asList("G6","H6"), gp8);
			Ship ship18 = new Ship(shipTypeDestroyer, Arrays.asList("B5","C5","D5"), gp9);
			Ship ship19 = new Ship(shipTypePatrolBoat, Arrays.asList("C6","C7"), gp9);
			Ship ship20 = new Ship(shipTypeSubmarine, Arrays.asList("A2","A3","A4"), gp10);
			Ship ship21 = new Ship(shipTypePatrolBoat, Arrays.asList("G6","H6"), gp10);
			Ship ship22 = new Ship(shipTypeDestroyer, Arrays.asList("B5","C5","D5"), gp11);
			Ship ship23 = new Ship(shipTypePatrolBoat, Arrays.asList("C6","C7"), gp11);
			Ship ship24 = new Ship(shipTypeDestroyer, Arrays.asList("B5","C5","D5"), gp13);
			Ship ship25 = new Ship(shipTypePatrolBoat, Arrays.asList("C6","C7"), gp13);
			Ship ship26 = new Ship(shipTypeSubmarine, Arrays.asList("A2","A3","A4"), gp14);
			Ship ship27 = new Ship(shipTypePatrolBoat, Arrays.asList("G6","H6"), gp14);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);
			shipRepository.save(ship10);
			shipRepository.save(ship11);
			shipRepository.save(ship12);
			shipRepository.save(ship13);
			shipRepository.save(ship14);
			shipRepository.save(ship15);
			shipRepository.save(ship16);
			shipRepository.save(ship17);
			shipRepository.save(ship18);
			shipRepository.save(ship19);
			shipRepository.save(ship20);
			shipRepository.save(ship21);
			shipRepository.save(ship22);
			shipRepository.save(ship23);
			shipRepository.save(ship24);
			shipRepository.save(ship25);
			shipRepository.save(ship26);
			shipRepository.save(ship27);


			//Game1
			Salvo salvo1 = new Salvo(1,Arrays.asList("B5", "C5", "F1"),gp1);
			Salvo salvo2 = new Salvo(1,Arrays.asList("B4", "B5", "B6"),gp2);

			Salvo salvo3 = new Salvo(2,Arrays.asList("F2", "D5"),gp1);
			Salvo salvo4 = new Salvo(2,Arrays.asList("E1", "H3", "A2"),gp2);

			//Game2
			Salvo salvo5 = new Salvo(1,Arrays.asList("A2", "A4", "G6"),gp3);
			Salvo salvo6 = new Salvo(1,Arrays.asList("B5", "D5", "C7"),gp4);

			Salvo salvo7 = new Salvo(2,Arrays.asList("A3", "H6"),gp3);
			Salvo salvo8 = new Salvo(2,Arrays.asList("C5", "C6"),gp4);

			//Game3
			Salvo salvo9 = new Salvo(1,Arrays.asList("G6", "H6", "A4"),gp5);
			Salvo salvo10 = new Salvo(1,Arrays.asList("H1", "H2", "H3"),gp6);

			Salvo salvo11 = new Salvo(2,Arrays.asList("A2", "A3", "D8"),gp5);
			Salvo salvo12 = new Salvo(2,Arrays.asList("E1", "F2", "G3"),gp6);

			//Game4
			Salvo salvo13 = new Salvo(1,Arrays.asList("A3", "A4", "F7"),gp7);
			Salvo salvo14 = new Salvo(1,Arrays.asList("B5", "C6", "H1"),gp8);

			Salvo salvo15 = new Salvo(2,Arrays.asList("A2", "G6", "H6"),gp7);
			Salvo salvo16 = new Salvo(2,Arrays.asList("C5", "C7", "D5"),gp8);

			//Game5
			Salvo salvo17 = new Salvo(1,Arrays.asList("A1", "A2", "A3"),gp9);
			Salvo salvo18 = new Salvo(1,Arrays.asList("B5", "B6", "C7"),gp10);

			Salvo salvo19 = new Salvo(2,Arrays.asList("G6", "G7", "G8"),gp9);
			Salvo salvo20 = new Salvo(2,Arrays.asList("C6", "D6", "E6"),gp10);

			//Game6
			Salvo salvo21 = new Salvo(3,Arrays.asList("H1", "H8"),gp11);


			salvoRepository.saveAll(Arrays.asList(salvo1, salvo2, salvo3, salvo4, salvo5, salvo6, salvo7, salvo8, salvo9, salvo10, salvo11, salvo12, salvo13, salvo14, salvo15, salvo16, salvo17, salvo18, salvo19, salvo20, salvo21));


			Score score1 = new Score((float) 0.5,date,p1,g1);
			Score score2 = new Score((float) 0.5,date,p2,g1);

			Score score3 = new Score((float) 0,date,p3,g2);
			Score score4 = new Score((float) 1,date,p4,g2);

			scoreRepository.saveAll(Arrays.asList(score1,score2,score3,score4));

		};
	}

}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName).orElse(null);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/index.html").permitAll()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/games").permitAll()
				.antMatchers("/api/players").permitAll()
				.antMatchers("/api/game_view/*").hasAuthority("USER")
				.antMatchers("/rest").denyAll()
				.antMatchers("/api/game_view/{gpid}").hasAuthority("USER")
				.anyRequest().permitAll();

		http.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());


	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}




