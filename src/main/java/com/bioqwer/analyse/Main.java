package com.bioqwer.analyse;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
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
				                           auth.getAccessToken())
				.execute();

		GetResponse getResponse = vk.wall().get()
				.domain("mudakoff")
				.filter(WallGetFilter.OWNER)
				.count(1)
				.offset(0)
				.execute();

		List<WallpostFull> items = getResponse.getItems();

		items.parallelStream()
				.forEach(w -> {
					List<WallComment> allComments = getAllWallComments(vk, w);
					map.put(w, allComments);
				});

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

	private static List<WallComment> getAllWallComments(VkApiClient vk, WallpostFull w) {
		Integer count = w.getComments().getCount();
		int size = count / 100 + 1;
		List<Integer> steps = IntStream.iterate(100, i -> i + 100)
				.limit(size)
				.boxed()
				.collect(Collectors.toList());

		List<WallComment> comments = new CopyOnWriteArrayList<>();

		return steps.parallelStream()
				.map(x -> {
					GetCommentsResponse commentsResponse = null;
					try {
						commentsResponse = vk.wall()
								.getComments(w.getId())
								.ownerId(w.getOwnerId())
								.extended(true)
								.count(100)
								.offset(x - 100)
								.needLikes(true)
								.execute();
					} catch (ApiException e) {
						e.printStackTrace();
					} catch (ClientException e) {
						e.printStackTrace();
					}
					return commentsResponse.getItems();
				})
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}
}
