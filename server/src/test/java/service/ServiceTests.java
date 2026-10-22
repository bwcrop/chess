package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    private AuthData registerUser(String username) throws Exception {
        return userService.register(new RegisterRequest(username, "password", username + "@mail.com"));
    }

    private int createGame(AuthData auth) throws Exception {
        return gameService.createGame(auth.authToken(), new CreateGameRequest("game")).gameID();
    }

    @Test
    void registerSuccess() throws Exception {
        AuthData auth = registerUser("alice");
        assertEquals("alice", auth.username());
        assertNotNull(auth.authToken());
        assertNotNull(userDAO.getUser("alice"));
    }

    @Test
    void registerUsernameTaken() throws Exception {
        registerUser("alice");
        assertThrows(AlreadyTakenException.class, () -> registerUser("alice"));
    }

    @Test
    void registerMissingField() {
        assertThrows(BadRequestException.class,
                () -> userService.register(new RegisterRequest("alice", null, "alice@mail.com")));
    }

    @Test
    void loginSuccess() throws Exception {
        AuthData registered = registerUser("alice");
        AuthData auth = userService.login(new LoginRequest("alice", "password"));
        assertEquals("alice", auth.username());
        assertNotEquals(registered.authToken(), auth.authToken());
    }

    @Test
    void loginWrongPassword() throws Exception {
        registerUser("alice");
        assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("alice", "wrong")));
    }

    @Test
    void loginUnknownUser() {
        assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("nobody", "password")));
    }

    @Test
    void loginMissingField() {
        assertThrows(BadRequestException.class,
                () -> userService.login(new LoginRequest("alice", null)));
    }

    @Test
    void logoutSuccess() throws Exception {
        AuthData auth = registerUser("alice");
        userService.logout(auth.authToken());
        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void logoutBadToken() {
        assertThrows(UnauthorizedException.class, () -> userService.logout("bad-token"));
    }

    @Test
    void listGamesSuccess() throws Exception {
        AuthData auth = registerUser("alice");
        createGame(auth);
        createGame(auth);
        assertEquals(2, gameService.listGames(auth.authToken()).games().size());
    }

    @Test
    void listGamesBadToken() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("bad-token"));
    }

    @Test
    void createGameSuccess() throws Exception {
        AuthData auth = registerUser("alice");
        int gameID = createGame(auth);
        GameData game = gameDAO.getGame(gameID);
        assertEquals("game", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void createGameMissingName() throws Exception {
        AuthData auth = registerUser("alice");
        assertThrows(BadRequestException.class,
                () -> gameService.createGame(auth.authToken(), new CreateGameRequest(null)));
    }

    @Test
    void createGameBadToken() {
        assertThrows(UnauthorizedException.class,
                () -> gameService.createGame("bad-token", new CreateGameRequest("game")));
    }

    @Test
    void joinGameSuccess() throws Exception {
        AuthData alice = registerUser("alice");
        AuthData bob = registerUser("bob");
        int gameID = createGame(alice);
        gameService.joinGame(alice.authToken(), new JoinGameRequest("WHITE", gameID));
        gameService.joinGame(bob.authToken(), new JoinGameRequest("BLACK", gameID));
        GameData game = gameDAO.getGame(gameID);
        assertEquals("alice", game.whiteUsername());
        assertEquals("bob", game.blackUsername());
    }

    @Test
    void joinGameColorTaken() throws Exception {
        AuthData alice = registerUser("alice");
        AuthData bob = registerUser("bob");
        int gameID = createGame(alice);
        gameService.joinGame(alice.authToken(), new JoinGameRequest("WHITE", gameID));
        assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(bob.authToken(), new JoinGameRequest("WHITE", gameID)));
    }

    @Test
    void joinGameBadColor() throws Exception {
        AuthData auth = registerUser("alice");
        int gameID = createGame(auth);
        assertThrows(BadRequestException.class,
                () -> gameService.joinGame(auth.authToken(), new JoinGameRequest("GREEN", gameID)));
    }

    @Test
    void joinGameNoSuchGame() throws Exception {
        AuthData auth = registerUser("alice");
        assertThrows(BadRequestException.class,
                () -> gameService.joinGame(auth.authToken(), new JoinGameRequest("WHITE", 999)));
    }

    @Test
    void joinGameBadToken() {
        assertThrows(UnauthorizedException.class,
                () -> gameService.joinGame("bad-token", new JoinGameRequest("WHITE", 1)));
    }

    @Test
    void clearSuccess() throws Exception {
        AuthData auth = registerUser("alice");
        createGame(auth);
        clearService.clear();
        assertNull(userDAO.getUser("alice"));
        assertNull(authDAO.getAuth(auth.authToken()));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}

