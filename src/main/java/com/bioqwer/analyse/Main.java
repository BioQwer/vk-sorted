package com.bioqwer.analyse;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.wall.WallGetFilter;

public class Main {

	private static final String CLIENT_SECRET = "YhhXgce0yrhyr3NqmztX";
	private static final int APP_ID = 5804106;

	private static Map<WallpostFull, List<WallComment>> map = new HashMap<>();

	public static void main(String[] args) throws ClientException, ApiException {
		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);

		ServiceClientCredentialsFlowResponse auth = vk.oauth()
				.serviceClientCredentialsFlow(APP_ID, CLIENT_SECRET)
				.execute();

		System.out.println(auth.getAccessToken());


		UserAuthResponse authResponse = vk.oauth()
				.userAuthorizationCodeFlow(APP_ID,
				                           CLIENT_SECRET,
				                           "https://oauth.vk.com/blank.html",
				                           "000d4c34d00333e1ec")
				.execute();

		System.out.println(authResponse);
		List<WallpostFull> items = getWallPost(vk);


		Comparator<Integer> compare = Integer::compare;
		final Comparator<Integer> reversed = compare.reversed();

		/*map.forEach((wallpostFull, wallComments) -> {
			System.out.printf("%s id = %d%n", wallpostFull.getText(), wallpostFull.getId());
			wallComments
					.stream()
					.sorted((o1, o2) -> reversed.compare(o1.getLikes().getCount(),
					                                     o2.getLikes().getCount()))
					.forEach(wallComment -> System.out.printf("like %s id %s %s %n",
				                                          wallComment.getLikes().getCount(),
				                                          wallComment.getFromId(),
				                                          wallComment.getText()));
			System.out.println((long) wallComments.size());
		});*/

		map.forEach((wallpostFull, wallComments) -> {
			int likes = wallComments.stream().mapToInt(x -> {
				return x.getLikes().getCount();
			}).sum();
			System.out.printf("%d likes %d %s  %n",
			                  wallpostFull.getId(),
			                  likes,
			                  wallpostFull.getText());
		});
	}

	private static List<WallpostFull> getWallPost(VkApiClient vk) throws ApiException, ClientException {
		GetResponse getResponse = vk.wall().get()
				.domain("mudakoff")
				.filter(WallGetFilter.OWNER)
				.count(1)
				.offset(0)
				.execute();

		return getResponse.getItems();
	}

}
