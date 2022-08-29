package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.SystemLib;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbWorker implements DbWorkerItf {

    private Connection dbConnexion;
    private List<Personne> listePersonnes;
    private int index = 0;

    /**
     * Constructeur du worker
     */
    public DbWorker() {
    }

    @Override
    public void connecterBdMySQL(String nomDB) throws MyDBException {
        final String url_local = "jdbc:mysql://localhost:3306/" + nomDB;
        final String url_remote = "jdbc:mysql://LAPEMFB37-21.edu.net.fr.ch:3306/" + nomDB;
        final String user = "root";
        final String password = "emf123";

        System.out.println("url:" + url_local);
        try {
            dbConnexion = DriverManager.getConnection(url_local, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdHSQLDB(String nomDB) throws MyDBException {
        final String url = "jdbc:hsqldb:file:" + nomDB + ";shutdown=true";
        final String user = "SA";
        final String password = "";
        System.out.println("url:" + url);
        try {
            dbConnexion = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdAccess(String nomDB) throws MyDBException {
        final String url = "jdbc:ucanaccess://" + nomDB;
        System.out.println("url=" + url);
        try {
            dbConnexion = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void deconnecter() throws MyDBException {
        try {
            if (dbConnexion != null) {
                dbConnexion.close();
            }
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    public List<Personne> lirePersonnes() throws MyDBException {
        listePersonnes = new ArrayList<>();
        try {
            Statement st = dbConnexion.createStatement();
            ResultSet rs = st.executeQuery("select PK_PERS, Nom, Prenom from t_personne");
            
            while(rs.next()){
                String nom = rs.getString("Nom");
                String prenom = rs.getString("Prenom");    
                Personne pers = new Personne(nom, prenom);
                listePersonnes.add(pers);
            }
            
        } catch (SQLException ex) {

        }
        return listePersonnes;
    }

    @Override
    public Personne precedentPersonne() throws MyDBException {
        listePersonnes = new ArrayList<>();

        if (listePersonnes == null) {
            lirePersonnes();
        }
        if (index >= 1) {
            index = index - 1;
        }
        
        return listePersonnes.get(index);

    }

    @Override
    public Personne suivantPersonne() throws MyDBException {
        listePersonnes = new ArrayList<>();

        if (listePersonnes == null) {
            lirePersonnes();
        }
        if (index < listePersonnes.size()) {
            index = index + 1;
        }
        
        return listePersonnes.get(index);

    }

}
