import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Julien on 16.06.15.
 */
public class Exec {

    public static void main(String[] args) throws IOException {

        ArrayList<String> cars = new ArrayList<String>();
        String url = "http://www.autoscout24.ch/fr/voitures/bmw--3-series?allmakes=1&kmto=70000&make=9,0,0&model=46,0,0&priceto=40000&st=2&vehtyp=10&yearfrom=2012";
        Elements nexts = null;
        do {
            if (url == "")
                break;

            System.out.println(url);
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.getElementsByClass("object-list-item");


            for (Element item : items) {

                Elements links = item.getElementsByTag("a");
                for (Element link : links) {
                    String linkHref = link.attr("abs:href");
                    if (!cars.contains(linkHref) && linkHref != "")
                        cars.add(linkHref);
                }
            }

            nexts = doc.getElementsByClass("next");

            if (nexts.size() == 1) {
                url = nexts.get(0).attr("abs:href");
            }


        } while (nexts.size() == 1);

        System.out.println("CARS : ");


        for (String car : cars) {
            ArrayList<String> equipement = new ArrayList<String>();
            String u = car + "&lng=fr&tabid=1";

            String[] params = u.split("&");
            Map<String, String> map = new HashMap<String, String>();
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }


            try {
                Elements es = Jsoup.connect(u).get().body().getElementsByClass("equipment-group").get(0).getElementsByClass("textlist-item");
                if (es == null)
                    System.out.println(u);
                for (Element e : es) {
                    if (!e.html().startsWith("http")) {
                        String t = StringEscapeUtils.unescapeHtml(e.html());
                        if (!equipement.contains(t))
                            equipement.add(t);
                    }
                }

                String id = map.get("vehid");

                new File("data2").mkdirs();

                PrintWriter pw = new PrintWriter("data2/" + id + ".txt");
                for (String s : equipement)
                    pw.println(s);
                pw.close();


                pw = new PrintWriter("data2/" + id + ".link.txt");

                pw.println(u);
                pw.close();


                /*
                pw = new PrintWriter("data/" + id + ".html");

                pw.println(Jsoup.connect(u).get().body());
                pw.close();*/
            } catch (Exception e) {
                System.out.println(u);
            }

        }


        //   System.out.println(content.html());
        /*
        Elements links = content.getElementsByTag("a");
        for (Element link : links) {
            String linkHref = link.attr("abs:href");
            String linkText = link.text();

            System.out.println(linkHref);
        }
        */
    }
}
