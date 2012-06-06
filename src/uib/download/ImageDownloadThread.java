package uib.download;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.utils.ImageUtils;
import org.apache.lucene.document.Document;

/**
 *
 * @author Olav
 */
public class ImageDownloadThread implements Runnable {

    private LinkedList<Document> finished;
    public final int NUMBER_OF_SYNC_DOWNLOADS = 5;
    private int currentIndex = 0;
    DocumentBuilder builder;
    public static ArrayList<String> images;
    protected final static String UIB_URL = "http://folk.uib.no/olo001/images/";

    /**
     * Creates a new instance of ImageDownloadThread
     *
     * @param parent
     */
    public ImageDownloadThread(DocumentBuilder builder) {
        this.builder = builder;
        finished = new LinkedList<Document>();
        images = new ArrayList<String>();
        images.add("A_Bar_at_the_Folies-Bergere.jpg");
        images.add("A_Foregone_Conclusion.jpg");
        images.add("A_Moonlit_River_Landscape_With_A_Figure.jpg");
        images.add("A_Recollection_of_October_5t.jpg");
        images.add("A_starry_night.jpg");
        images.add("A_Sunday_on_La_Grande_Jatte.jpg");
        images.add("Abigail_and_Nabal.jpg");
        images.add("Albion_Rose.jpg");
        images.add("Ancient_Rome_Agrippina_Landing_with_the_Ashes_of_Germanicus.jpg");
        images.add("Angler.jpg");
        images.add("Antibes.jpg");
        images.add("Autumn_effect_at_Argenteuil.jpg");
        images.add("Beach_at_Gravelines.jpg");
        images.add("Beach_scene_with_cart_and_horse.jpg");
        images.add("Beaumes.jpg");
        images.add("Break-away.jpg");
        images.add("Building_site_with_willows.jpg");
        images.add("Cafe_on_the_boulevard_-_Cafe'_du_Soleil_Martigues.jpg");
        images.add("CarlGustavCarus.jpg");
        images.add("Castle_by_the_River.jpg");
        images.add("Catspaws_off_the_Land.jpg");
        images.add("Christ_driving_the_moneylenders_from_the_Temple.jpg");
        images.add("Christ_in_the_wilderness.jpg");
        images.add("Cloud_study.jpg");
        images.add("Copy_after_a_self-portrait_by_Thomas_Gainsborough.jpg");
        images.add("Corot_View_at_Riva_Italian_Tyrol.jpg");
        images.add("Dance_at_Moulin_de_la_Galette.jpg");
        images.add("Dancer_on_a_stage.jpg");
        images.add("Distant_hill_with_clouds.jpg");
        images.add("Estuary.jpg");
        images.add("Evans_Bay.jpg");
        images.add("Farm_in_Normandy_-_enclosure.jpg");
        images.add("Fishermen_at_sea.jpg");
        images.add("Fishermen_at_sea2.jpg");
        images.add("Forest_path_near_Spandau.jpg");
        images.add("Hands.jpg");
        images.add("Hawes_-_Wensleydale.jpg");
        images.add("Haymaking.jpg");
        images.add("Horses_in_the_water.jpg");
        images.add("In_the_Conservatory.jpg");
        images.add("In_the_ploughed_field_-_Spring.jpg");
        images.add("Italian_soldier_no._2.jpg");
        images.add("Janus_and_Saturn.jpg");
        images.add("John_-_Baron_Craven_of_Ryton.jpg");
        images.add("Lake_grunewald.jpg");
        images.add("Lake_in_the_Riesengebirge.jpg");
        images.add("Landscape_-_capriccio.jpg");
        images.add("Landscape_-_edge_of_a_wood.jpg");
        images.add("Landscape_at_Louveciennes.jpg");
        images.add("Landscape_under_snow.jpg");
        images.add("Landscape_with_Solitary_Tree.jpg");
        images.add("Le_brusq.jpg");
        images.add("Llyn-y-Cau,_Cader_Idris.jpg");
        images.add("Market_in_Tunis.jpg");
        images.add("Mast_tree_grove.jpg");
        images.add("Mealtime_prayer.jpg");
        images.add("Mill_on_the_Couleuvre_at_Pontoise.jpg");
        images.add("Moonrise_over_the_Sea.jpg");
        images.add("Morning.jpg");
        images.add("Moscow_patio.jpg");
        images.add("New_Rome_-_The_Castle_of_S.Angelo.jpg");
        images.add("Nocturne-_Blue_and_Silver-Battersea_Reach.jpg");
        images.add("On_Outpost_Duty.jpg");
        images.add("Ophelia.jpg");
        images.add("Outskirts_of_Pont-Aven.jpg");
        images.add("Palm_tree.jpg");
        images.add("Pancake_Week.jpg");
        images.add("Parable_of_the_Sower_of_Tares.jpg");
        images.add("Peach_Trees_in_Blossom.jpg");
        images.add("Polperro.jpg");
        images.add("Ponies_on_the_shore_-_Carmargues.jpg");
        images.add("Poppy_Field_.jpg");
        images.add("Portrait_of_a_lady.jpg");
        images.add("Portrait_of_a_man.jpg");
        images.add("Portrait_of_a_man_with_a_skull.jpg");
        images.add("Portrait_of_a_Royalist_officer.jpg");
        images.add("Portrait_of_a_young_woman.jpg");
        images.add("Portrait_of_Charles_Tudway-MP.jpg");
        images.add("Portrait_of_Hernan_Cortez.jpg");
        images.add("Portrait_of_Jan_van_Montfort.jpg");
        images.add("Portrait_of_Lady_Witt.jpg");
        images.add("Portrait_of_Lord_Conway_of_Allington.jpg");
        images.add("Portrait_of_Mrs_Cyprian_Williams.jpg");
        images.add("Portrait_of_Sir_Robert_Wit.jpg");
        images.add("Rain_cloud.jpg");
        images.add("Reuben_Presenting_Mandrakes_to_Leah.jpg");
        images.add("River_Dee.jpg");
        images.add("Rye.jpg");
        images.add("Saint_Charles_Borromeo_meditating_on_the_Crucifix.jpg");
        images.add("Saint_Germain_l'Auxerrois_Paris.jpg");
        images.add("Saint_Gregory_the_Great.jpg");
        images.add("Saint_Tropez.jpg");
        images.add("Seine_Landscape_near_Chatou.jpg");
        images.add("Sketch_for_a_female_head.jpg");
        images.add("Sketching_On_The_Lake.jpg");
        images.add("Spring_landscape.jpg");
        images.add("Still_life_in_the_kitchen.jpg");
        images.add("Still_life_with_anenomes.jpg");
        images.add("Still_life_with_bottles_and_Breton_bonnets.jpg");
        images.add("Still_life_with_bowl_of_fruit_by_a_window.jpg");
        images.add("Still_life_with_dahlias.jpg");
        images.add("Still_life_with_eggs.jpg");
        images.add("Still_life_with_glass_jar_and_silver_box.jpg");
        images.add("Still_life_with_peaches_and_pears.jpg");
        images.add("Still_life_with_pears.jpg");
        images.add("Suicide_of_Lucretia.jpg");
        images.add("Summer.jpg");
        images.add("Summer-Monet.jpg");
        images.add("The_apotheosis_of_war.jpg");
        images.add("The_beach_at_sainte_adresse.jpg");
        images.add("The_Card_Players.jpg");
        images.add("The_Deluge.jpg");
        images.add("The_Dream.jpg");
        images.add("The_Fighting_Temeraire_tugged_to_her_last_Berth_to_be_broken.jpg");
        images.add("The_flax_barn_at_laren.jpg");
        images.add("The_Forge.jpg");
        images.add("The_Forum_at_Pompeii_with_Vesuvius_in_the_Background.jpg");
        images.add("The_Granite_Dish_in_the_Berlin_Lustgarten.jpg");
        images.add("The_Heroes.jpg");
        images.add("The_hill_of_Montmartre_with_stone_quarry.jpg");
        images.add("The_Isle_of_the_Dead.jpg");
        images.add("The_la_rue_bavolle_at_honfleur.jpg");
        images.add("The_Lady_of_Shallot.jpg");
        images.add("The_Montagne_Sainte-Victoire_with_Large_Pine.jpg");
        images.add("The_neustadt_eberswalde_rolling_mill.jpg");
        images.add("The_Plains_of_Heaven.jpg");
        images.add("The_Road_from_Versailles_to_Saint-Germain.jpg");
        images.add("The_rowers.jpg");
        images.add("The_sleeping_gipsy.jpg");
        images.add("The_Solitude_-_Recollection_of_Vigen_-_Limousin.jpg");
        images.add("The_Spring_Pilgrimage_of_the_Tsarina_under_Tsar_Aleksy.jpg");
        images.add("The_Thaw.jpg");
        images.add("The_Vale_of_Rest.jpg");
        images.add("The_walker.jpg");
        images.add("The_wave.jpg");
        images.add("Toll_gate.jpg");
        images.add("Triptych_The_Descent_from_the_Cross.jpg");
        images.add("Triumph_of_Scipio_Africanus.jpg");
        images.add("Turning_Road_at_Montgeroult.jpg");
        images.add("Two_dogs.jpg");
        images.add("Two_heads_of_angels.jpg");
        images.add("Vertumnus_and_Pomona.jpg");
        images.add("Via_appia_at_sunset.jpg");
        images.add("View_of_Verona.jpg");
        images.add("Walk_Road_of_the_Farm_Saint-Simeon.jpg");
        images.add("Wet_Meadow.jpg");
        images.add("Wiew_of_Roofs_and_Gardens.jpg");
        images.add("Women_going_to_the_woods.jpg");
        images.add("Young_woman_in_a_landscape_-_Carinthia.jpg");
        images.add("Zonnebeke.jpg");
    }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_SYNC_DOWNLOADS);
        for (String image : images) {
            pool.execute(new SingleImageThread(image, this));
        }
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Warning: Exception occured while downloading images " + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (currentIndex + 1 < images.size()) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("WARNING: " + (images.size() - currentIndex) + " images left");
            currentIndex = images.size();
        }
    }

    public synchronized Document getCurrentDoc() {
        if (currentIndex < (images.size() - 1)) {
            currentIndex++;
            while (finished.size() < 1) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return finished.removeFirst();
        } else {
            return null;
        }
    }

    public DocumentBuilder getDocumentBuilder() {
        return builder;
    }

    public void addDocumentToFinished(Document doc) {
        if (doc == null) {
            currentIndex++;
        } else {
            finished.add(doc);
        }
    }
}

class SingleImageThread implements Runnable {

    String imageUrl;
    ImageDownloadThread idt;

    SingleImageThread(String imageUrl, ImageDownloadThread idt) {
        this.imageUrl = imageUrl;
        this.idt = idt;
    }

    @Override
    public void run() {
        try {
            BufferedImage image = ImageIO.read(new URL(ImageDownloadThread.UIB_URL + imageUrl));
            image = ImageUtils.scaleImage(image, 1024);
            File cachedImage = new File(ImageIndexingThread.installPath + ImageIndexingThread.cacheDirectory + imageUrl);
            ImageIO.write(image, "jpg", cachedImage);
            Document doc = idt.getDocumentBuilder().createDocument(image, cachedImage.getAbsolutePath());


            idt.addDocumentToFinished(doc);
        } catch (IOException e) {
            System.out.println("Warning: Exception reading & indexing image " + ": " + e.getMessage());
            idt.addDocumentToFinished(null);
        } /*
         * About 1 in 25 - 30 images use a corrpted colorspace causing an error.
         * Using JAI - Java advance imageing can be a solution, but since
         * flicker download is an extra solution this is a priority
         */ catch (java.awt.color.CMMException cmme) {
            System.out.println("Warning: Exception reading & indexing image " + "Corrupted fileformat" + ": " + cmme.getMessage());
            idt.addDocumentToFinished(null);

        }
    }
}
