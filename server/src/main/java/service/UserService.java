package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(RegisterRequest request)
            throws DataAccessException, BadRequestException, AlreadyTakenException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException();
        }
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException();
        }
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        userDAO.createUser(new UserData(request.username(), hashedPassword, request.email()));
        return createAuth(request.username());
    }

    public AuthData login(LoginRequest request)
            throws DataAccessException, BadRequestException, UnauthorizedException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException();
        }
        UserData user = userDAO.getUser(request.username());
        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException();
        }
        return createAuth(user.username());
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(authToken);
    }

    private AuthData createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authDAO.createAuth(auth);
        return auth;
    }
}
