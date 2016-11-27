import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by white on 2016-11-27.
 */
public class Main {

    public static void main(String[] args){
        clarifaiEngin(Constant.SAMPLE_IMAGE);
        imaggaEngin(Constant.SAMPLE_IMAGE);
    }


    public static void clarifaiEngin(String url){

        final ClarifaiClient client =
                new ClarifaiBuilder(Constant.CLARIFAI_CLIENT_ID, Constant.CLARIFAI_CLIENT_SECRET).buildSync();
                client.getDefaultModels().generalModel()
                        .predict()
                        .withInputs(
                                ClarifaiInput.forImage(ClarifaiImage.of(url))
                        ).executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
                    public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
                        System.out.println("CLARIFAI ENGIN :");

                        List<Concept> concepts=clarifaiOutputs.get(0).data();

                        for(Concept c:concepts){
                            System.out.println("name: "+c.name()+" value:"+c.value());
                        }
                    }

                    public void onClarifaiResponseUnsuccessful(int i) {

                    }

                    public void onClarifaiResponseNetworkError(IOException e) {

                    }
                });
    }

    public static void imaggaEngin(String url){

         Unirest.get("https://api.imagga.com/v1/tagging")
                .queryString("url", url)
                .basicAuth(Constant.IMAGGA_CLIENT_ID, Constant.IMAGGA_CLIENT_SECRET)
                .header("Accept", "application/json")
                .asJsonAsync(new Callback<JsonNode>() {
                    public void completed(HttpResponse<JsonNode> httpResponse) {
                        System.out.println("IMAGGA ENGIN :");
                        JSONObject object=httpResponse.getBody().getObject();
                        JSONArray tags=object.getJSONArray("results").getJSONObject(0).getJSONArray("tags");

                        for(int i=0;i<tags.length();i++){
                            System.out.println("name: "+tags.getJSONObject(i).get("tag")+" value:"+(tags.getJSONObject(i).getDouble("confidence")/100));
                        }

                    }

                    public void failed(UnirestException e) {

                    }

                    public void cancelled() {

                    }
                });
    }
}
