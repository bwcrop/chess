package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.ClearService;
import service.CreateGameRequest;
import service.GameService;
import service.JoinGameRequest;
import service.LoginRequest;
import service.RegisterRequest;
import service.UnauthorizedException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);

        javalin.exception(BadRequestException.class, (e, ctx) -> sendError(ctx, 400, e));
        javalin.exception(UnauthorizedException.class, (e, ctx) -> sendError(ctx, 401, e));
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> sendError(ctx, 403, e));
        javalin.exception(Exception.class, (e, ctx) -> sendError(ctx, 500, e));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx) throws Exception {
        clearService.clear();
        sendJson(ctx, Map.of());
    }

    private void register(Context ctx) throws Exception {
        sendJson(ctx, userService.register(parse(ctx, RegisterRequest.class)));
    }

    private void login(Context ctx) throws Exception {
        sendJson(ctx, userService.login(parse(ctx, LoginRequest.class)));
    }

    private void logout(Context ctx) throws Exception {
        userService.logout(ctx.header("authorization"));
        sendJson(ctx, Map.of());
    }

    private void listGames(Context ctx) throws Exception {
        sendJson(ctx, gameService.listGames(ctx.header("authorization")));
    }

    private void createGame(Context ctx) throws Exception {
        CreateGameRequest request = parse(ctx, CreateGameRequest.class);
        sendJson(ctx, gameService.createGame(ctx.header("authorization"), request));
    }

    private void joinGame(Context ctx) throws Exception {
        JoinGameRequest request = parse(ctx, JoinGameRequest.class);
        gameService.joinGame(ctx.header("authorization"), request);
        sendJson(ctx, Map.of());
    }

    private <T> T parse(Context ctx, Class<T> type) throws BadRequestException {
        T request;
        try {
            request = gson.fromJson(ctx.body(), type);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException();
        }
        if (request == null) {
            throw new BadRequestException();
        }
        return request;
    }

    private void sendJson(Context ctx, Object body) {
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(body));
    }

    private void sendError(Context ctx, int status, Exception e) {
        ctx.status(status);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
    }
}

