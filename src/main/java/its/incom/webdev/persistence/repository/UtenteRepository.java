package its.incom.webdev.persistence.repository;

import its.incom.webdev.persistence.model.EsitoCandidatura;
import its.incom.webdev.persistence.model.Ruolo;
import its.incom.webdev.persistence.model.Utente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UtenteRepository {

    private final DataSource database;

    public UtenteRepository(DataSource database) {
        this.database = database;
    }

    public Utente createUtente(Utente utente) throws SQLException {
        if (checkUtente(utente.getEmail(), utente.getPasswordHash())) {
            throw new BadRequestException("Utente già esistente");
        }

        String query = "INSERT INTO Utente (nome, cognome, email, pswHash, data_registrazione, ruolo) VALUES (?, ?, ?, ?, ?, 'S')";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, utente.getNome());
            statement.setString(2, utente.getCognome());
            statement.setString(3, utente.getEmail());
            statement.setString(4, utente.getPasswordHash());
            statement.setObject(5, utente.getDataRegistrazione());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la creazione dell'utente", e);
        }

        return utente;
    }


    private boolean checkUtente(String email, String pswHash) {
        String query = "SELECT COUNT(*) FROM Utente WHERE email = ? AND pswHash = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, pswHash);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            // Log the exception (use a logging framework or print the stack trace)
            e.printStackTrace();
            throw new RuntimeException("Errore durante il controllo dell'utente", e);
        }
    }

    //controllare se non servono le altre info dell'utente
    public Optional<Utente> findByEmailPsw(String email, String pswHash) {
        try {
            try (Connection connection = database.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT id,email,pswHash,ruolo FROM Utente WHERE email = ? AND pswHash = ?")) {
                    statement.setString(1, email);
                    statement.setString(2, pswHash);
                    var resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        var utente = new Utente();
                        utente.setId(resultSet.getInt("id"));
                        utente.setEmail(resultSet.getString("email"));
                        utente.setPasswordHash(resultSet.getString("pswHash"));
                        utente.setRuolo(Ruolo.valueOf(resultSet.getString("ruolo")));
                        return Optional.of(utente);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Utente> findByEmail(String email) {
        try {
            try (Connection connection = database.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT id,email,pswHash FROM Utente WHERE email = ?")) {
                    statement.setString(1, email);
                    var resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        var utente = new Utente();
                        utente.setId(resultSet.getInt("id"));
                        utente.setEmail(resultSet.getString("email"));
                        utente.setPasswordHash(resultSet.getString("pswHash"));
                        return Optional.of(utente);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Utente> getUtenteById(int id) {
        String query = "SELECT * FROM Utente WHERE id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Utente utente = new Utente();
                    utente.setId(resultSet.getInt("id"));
                    utente.setNome(resultSet.getString("nome"));
                    utente.setCognome(resultSet.getString("cognome"));
                    utente.setEmail(resultSet.getString("email"));
                    utente.setPasswordHash(resultSet.getString("pswHash"));
                    utente.setDataRegistrazione(resultSet.getObject("data_registrazione", LocalDate.class));
                    Ruolo ruolo = Ruolo.valueOf(resultSet.getString("ruolo").toUpperCase());
                    utente.setRuolo(ruolo);

                    return Optional.of(utente);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            // Log the exception (use a logging framework or print the stack trace)
            e.printStackTrace();
            throw new RuntimeException("Errore durante la ricerca dell'utente", e);
        }
    }
    public List<Utente> getUtenti(){
        List<Utente> list=new ArrayList<>();
        String query = "SELECT id,nome,cognome,email,data_registrazione,ruolo FROM Utente";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    Utente utente = new Utente();
                    utente.setId(resultSet.getInt("id"));
                    utente.setNome(resultSet.getString("nome"));
                    utente.setCognome(resultSet.getString("cognome"));
                    utente.setEmail(resultSet.getString("email"));
                    utente.setDataRegistrazione(resultSet.getObject("data_registrazione", LocalDate.class));
                    //prendo l'esito in stringa la formatto per ENUM di Java
                    Ruolo ruolo = Ruolo.valueOf(resultSet.getString("ruolo").toUpperCase());
                    utente.setRuolo(ruolo);
                    list.add(utente);
                }return list;
            }
        } catch (SQLException e) {
            // Log the exception (use a logging framework or print the stack trace)
            e.printStackTrace();
            throw new RuntimeException("Errore durante la selezione degli utenti", e);
        }
    }
    public void setRuolo(int id,String ruolo){
        String query = "UPDATE Utente SET ruolo = ? WHERE id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ruolo);
            statement.setInt(2, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                //eccezione personalizzata mancante
                throw new RuntimeException("Nessun utente trovato con l'ID specificato");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'aggiornamento dell'utente", e);
        }
    }


}
