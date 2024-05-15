package com.example.havenwomansafetyapp;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Resources extends Activity {

    private ListView listView;
    private String[] resourceTitles = {
            "Stay Safe in Public: Tips and Guidelines",
            "National Crime Prevention Council (NCPC)",
            "Garda Síochána - Ireland",
            "European Institute for Crime Prevention and Control (HEUNI)"
    };

    private String[] resourceLinks = {
            "https://www.cbp.gov/employee-resources/staying-safe-public-tips",
            "https://www.ncpc.org/",
            "https://www.garda.ie/en/",
            "https://www.unodc.org/unodc/en/commissions/CCPCJ/PNI/institutes-HEUNI.html"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        listView = findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, resourceTitles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openLink(resourceLinks[position]);
            }
        });
    }

    private void openLink(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }
}
