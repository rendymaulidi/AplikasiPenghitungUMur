import java.time.LocalDate;
import java.time.Period;
import java.io.BufferedReader; 
import java.io.InputStreamReader; 
import java.net.HttpURLConnection; 
import java.net.URL; 
import java.util.function.Supplier; 
import javax.swing.JTextArea; 
import org.json.JSONArray; 
import org.json.JSONObject; 

public class PenghitungUmurHelper {
    
    // Mendapatkan peristiwa penting secara baris per baris 
public void getPeristiwaBarisPerBaris(LocalDate tanggal, JTextArea 
txtAreaPeristiwa, Supplier<Boolean> shouldStop) { 
    try { 
        // Periksa jika thread seharusnya dihentikan sebelum dimulai 
        if (shouldStop.get()) { 
            return; 
        } 
 
        String urlString = "https://byabbe.se/on-this-day/" + tanggal.getMonthValue() + "/" + tanggal.getDayOfMonth() + "/events.json"; 
        URL url = new URL(urlString); 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
        conn.setRequestMethod("GET"); 
        conn.setRequestProperty("User-Agent", "Mozilla/5.0"); 
 
        int responseCode = conn.getResponseCode(); 
        if (responseCode != 200) { 
            throw new Exception("HTTP response code: " + responseCode + ". Silakan coba lagi nanti atau cek koneksi internet."); 
        } 
 
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
        String inputLine; 
        StringBuilder content = new StringBuilder(); 
        while ((inputLine = in.readLine()) != null) { 
            // Periksa jika thread seharusnya dihentikan saat membaca data 
            if (shouldStop.get()) { 
                in.close(); 
                conn.disconnect(); 
                javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.setText("Pengambilan data dibatalkan.\n")); 
                return; 
            } 
            content.append(inputLine); 
        } 
        in.close(); 
        conn.disconnect(); 
 
        JSONObject json = new JSONObject(content.toString()); 
        JSONArray events = json.getJSONArray("events"); 
        for (int i = 0; i < events.length(); i++) { 
            // Periksa jika thread seharusnya dihentikan sebelum memproses data 
            if (shouldStop.get()) { 
                javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.setText("Pengambilan data dibatalkan.\n")); 
                return; 
            } 
 
            JSONObject event = events.getJSONObject(i); 
            String year = event.getString("year"); 
            String description = event.getString("description"); 
            String translatedDescription = translateToIndonesian(description); 
            String peristiwa = year + ": " + translatedDescription; 
            String peristiwaBaru = year + ": " + description; 
            javax.swing.SwingUtilities.invokeLater(() -> 
           txtAreaPeristiwa.append(peristiwa + "\n")); 
        } 
 
        if (events.length() == 0) { 
            javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.setText("Tidak ada peristiwa penting yang ditemukan pada tanggal ini.")); 
        } 
    } catch (Exception e) { 
        javax.swing.SwingUtilities.invokeLater(() -> txtAreaPeristiwa.setText("Gagal mendapatkan data peristiwa: " + e.getMessage())); 
    } 
} 


    // Menghitung umur secara detail (tahun, bulan, hari)
    public String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) {
        Period period = Period.between(lahir, sekarang);
        return period.getYears() + " tahun, " + period.getMonths() + " bulan, " + period.getDays() + " hari";
    }

    // Menghitung hari ulang tahun berikutnya
    public LocalDate hariUlangTahunBerikutnya(LocalDate lahir, LocalDate sekarang) {
        LocalDate ulangTahunBerikutnya = lahir.withYear(sekarang.getYear());
        if (!ulangTahunBerikutnya.isAfter(sekarang)) {
            ulangTahunBerikutnya = ulangTahunBerikutnya.plusYears(1);
        }
        return ulangTahunBerikutnya;
    }

    // Menerjemahkan teks hari ke bahasa Indonesia
    public String getDayOfWeekInIndonesian(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY: return "Senin";
            case TUESDAY: return "Selasa";
            case WEDNESDAY: return "Rabu";
            case THURSDAY: return "Kamis";
            case FRIDAY: return "Jumat";
            case SATURDAY: return "Sabtu";
            case SUNDAY: return "Minggu";
            default: return "";
        }
    }
    // Menerjemahkan teks ke bahasa Indonesia 
private String translateToIndonesian(String text) { 
    try { 
        String urlString = "https://lingva.ml/api/v1/en/id/" + 
text.replace(" ", "%20"); 
        URL url = new URL(urlString); 
        HttpURLConnection conn = (HttpURLConnection) 
url.openConnection(); 
        conn.setRequestMethod("GET"); 
        conn.setRequestProperty("User-Agent", "Mozilla/5.0"); 
 
        int responseCode = conn.getResponseCode(); 
        if (responseCode != 200) { 
            throw new Exception("HTTP response code: " + responseCode); 
        } 
 
        BufferedReader in = new BufferedReader(new 
InputStreamReader(conn.getInputStream(), "utf-8")); 
        String inputLine; 
        StringBuilder content = new StringBuilder(); 
        while ((inputLine = in.readLine()) != null) { 
            content.append(inputLine); 
        } 
        in.close(); 
        conn.disconnect(); 
 
        JSONObject json = new JSONObject(content.toString()); 
        return json.getString("translation"); 
    } catch (Exception e) { 
        return text + " (Gagal diterjemahkan)"; 
    } 
}
}