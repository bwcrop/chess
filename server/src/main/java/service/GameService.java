package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException, UnauthorizedException {
        authorize(authToken);
        return new ListGamesResult(gameDAO.listGames());
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws DataAccessException, UnauthorizedException, BadRequestException {
        authorize(authToken);
        if (request.gameName() == null) {
            throw new BadRequestException();
        }
        return new CreateGameResult(gameDAO.createGame(request.gameName()));
    }

    public void joinGame(String authToken, JoinGameRequest request)
            throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        AuthData auth = authorize(authToken);
        String color = request.playerColor();
        if (request.gameID() == null || !("WHITE".equals(color) || "BLACK".equals(color))) {
            throw new BadRequestException();
        }
        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException();
        }

        boolean joiningWhite = color.equals("WHITE");
        String currentPlayer = joiningWhite ? game.whiteUsername() : game.blackUsername();
        if (currentPlayer != null) {
            throw new AlreadyTakenException();
        }

        String white = joiningWhite ? auth.username() : game.whiteUsername();
        String black = joiningWhite ? game.blackUsername() : auth.username();
        gameDAO.updateGame(new GameData(game.gameID(), white, black, game.gameName(), game.game()));
    }

    private AuthData authorize(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException();
        }
        return auth;
    }
}
