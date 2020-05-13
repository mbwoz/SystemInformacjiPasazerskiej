package systeminformacjipasazerskiej.db;

import systeminformacjipasazerskiej.converter.DayConverter;
import systeminformacjipasazerskiej.model.Kurs;
import systeminformacjipasazerskiej.model.Postoj;
import systeminformacjipasazerskiej.model.Stacja;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class QueryDBService {
    private Connection connection;

    public QueryDBService(Connection connection) {
        this.connection = connection;
    }

    public Stacja getStationById(int idStacji) {
        Stacja stacja = new Stacja();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM stacje WHERE id_stacji = " + idStacji + ";"
            );

            if(resultSet.next()) {
                stacja.setIdStacji(resultSet.getInt("id_stacji"));
                stacja.setNazwaStacji(resultSet.getString("nazwa_stacji"));
                stacja.setLiczbaTorow(resultSet.getInt("liczba_torow"));
                stacja.setLiczbaPeronow(resultSet.getInt("liczba_peronow"));
                stacja.setDlugoscPeronu(resultSet.getDouble("dlugosc_peronu"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stacja;
    }

    public ArrayList<Stacja> getAllStations() {
        ArrayList<Stacja> stacje = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM stacje ORDER BY nazwa_stacji;"
            );

            while(resultSet.next()) {
                Stacja stacja = new Stacja();
                stacja.setIdStacji(resultSet.getInt("id_stacji"));
                stacja.setNazwaStacji(resultSet.getString("nazwa_stacji"));
                stacja.setLiczbaTorow(resultSet.getInt("liczba_torow"));
                stacja.setLiczbaPeronow(resultSet.getInt("liczba_peronow"));
                stacja.setDlugoscPeronu(resultSet.getDouble("dlugosc_peronu"));

                stacje.add(stacja);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stacje;
    }

    public ArrayList<Kurs> getConnections(String fromStation, String toStation, String day, String time) {
        ArrayList<Kurs> kursy = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT id_stacji FROM stacje WHERE nazwa_stacji = '" + fromStation + "';"
            );
            int fromStationId = resultSet.next() ? resultSet.getInt("id_stacji") : -1;

            resultSet = statement.executeQuery(
                    "SELECT id_stacji FROM stacje WHERE nazwa_stacji = '" + toStation + "';"
            );
            int toStationId = resultSet.next() ? resultSet.getInt("id_stacji") : -1;

            System.out.println("id_stacji ready " + fromStationId + " " + toStationId);
            System.out.println(day + " " + DayConverter.convertDay(day));

            resultSet = statement.executeQuery(
                    "SELECT ro.* " +
                            "FROM rozklady ro " +
                            "INNER JOIN postoje pos ON ro.id_kursu = pos.id_kursu " +
                            "WHERE getDay(ro.id_kursu, " + fromStationId + ") IS NOT DISTINCT FROM " +
                            DayConverter.convertDay(day) + " AND " +
                            "pos.id_stacji = " + fromStationId + " AND pos.odjazd >= '" + time + "'::time AND " +
                            "ro.id_pociagu IN " +
                            "(SELECT po.id_pociagu " +
                            "FROM pociagi po " +
                            "WHERE po.id_trasy IN " +
                            "(SELECT idTrasy " +
                            "FROM getIdTrasyFromTo(" + fromStationId + ", " + toStationId + "))) " +
                            "ORDER BY pos.odjazd;"
            );

            while(resultSet.next()) {
                Kurs kurs = new Kurs();
                kurs.setIdKursu(resultSet.getInt("id_kursu"));
                kurs.setIdPociagu(resultSet.getInt("id_pociagu"));

                kursy.add(kurs);
            }

            System.out.println("kursy ready, size " + kursy.size());

            for(Kurs kurs : kursy) {
                resultSet = statement.executeQuery(
                        "SELECT pos.* " +
                                "FROM " +
                                "(SELECT idStacji " +
                                "FROM getStationsBetween(" + kurs.getIdKursu() + ", " + fromStationId + ", " + toStationId + ")) st " +
                                "INNER JOIN postoje pos ON st.idStacji = pos.id_stacji " +
                                "WHERE pos.id_kursu = " + kurs.getIdKursu() + ";"
                );

                ArrayList<Postoj> listaPostojow = new ArrayList<>();

                while(resultSet.next()) {
                    Postoj postoj = new Postoj();
                    postoj.setIdKursu(resultSet.getInt("id_kursu"));
                    postoj.setStacja(getStationById(resultSet.getInt("id_stacji")));
                    postoj.setPrzyjazd(resultSet.getString("przyjazd"));
                    postoj.setOdjazd(resultSet.getString("odjazd"));
                    postoj.setNastepnySklad(resultSet.getInt("nastepny_sklad"));

                    listaPostojow.add(postoj);
                }

                kurs.setListaPostojow(listaPostojow);

                System.out.println("added postoje for kurs " + kurs.getIdKursu());
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kursy;
    }

}
