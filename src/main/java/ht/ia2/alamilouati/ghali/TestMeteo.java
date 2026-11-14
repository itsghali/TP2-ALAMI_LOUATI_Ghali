package ht.ia2.alamilouati.ghali;

import ht.ia2.alamilouati.ghali.outil.meteo.MeteoTool;

public class TestMeteo {
    public static void main(String[] args) {
        if (args.length != 0 && args.length != 4) {
            throw new IllegalArgumentException("Arguments : latitude longitude nbJoursDebut nbJours");
        }
        double latitude = args.length == 4 ? Double.parseDouble(args[0]) : 48.85;
        double longitude = args.length == 4 ? Double.parseDouble(args[1]) : 2.35;
        int debut = args.length == 4 ? Integer.parseInt(args[2]) : 0;
        int duree = args.length == 4 ? Integer.parseInt(args[3]) : 3;

        boolean pluie = new MeteoTool().isPluiePrevue(latitude, longitude, debut, duree);
        System.out.printf("Lieu : %.5f, %.5f ; debut : J+%d ; duree : %d jours%n",
                latitude, longitude, debut, duree);
        System.out.println("Probabilite de pluie superieure a 25 % sur la periode : " + pluie);
    }
}
