package com.bioqwer.analyse.vk;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.wall.WallGetFilter;

public class VkApiWorker {


	private final TransportClient _transportClient;
	private final VkApiClient _vk;

	Comparator<Integer> compare = Integer::compare;
	final Comparator<Integer> reversed = compare.reversed();


	public VkApiWorker() {
		_transportClient = HttpTransportClient.getInstance();
		_vk = new VkApiClient(_transportClient);
	}

	public List<WallComment> getAllWallComments(WallpostFull wallpostFull) {
		Integer count = wallpostFull.getComments().getCount();
		int size = count / 100 + 1;
		List<Integer> steps = IntStream.iterate(100, i -> i + 100)
				.limit(size)
				.boxed()
				.collect(Collectors.toList());

		return steps.parallelStream()
				.map(x -> {
					GetCommentsResponse commentsResponse = null;
					try {
						commentsResponse = _vk.wall()
								.getComments(wallpostFull.getId())
								.ownerId(wallpostFull.getOwnerId())
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

	public List<WallComment> sortByLikes(List<WallComment> wallComments) {
		return wallComments
				.stream()
				.sorted((o1, o2) -> reversed.compare(o1.getLikes().getCount(),
				                                     o2.getLikes().getCount()))
				.collect(Collectors.toList());
	}

	public List<WallpostFull> getLastWallPost(String vkPublic, int lastCount) throws ApiException, ClientException {
		System.out.println(vkPublic);
		GetResponse getResponse = _vk.wall().get()
				.domain(vkPublic)
				.filter(WallGetFilter.OWNER)
				.count(lastCount)
				.offset(0)
				.execute();
		return getResponse.getItems();
	}

}
