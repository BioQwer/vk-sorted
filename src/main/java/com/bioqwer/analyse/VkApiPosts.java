package com.bioqwer.analyse;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.WallpostFull;

@Path("post")
public class VkApiPosts {

	private VkApiWorker _vkApiWorker = new VkApiWorker();


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PostsKeeper> sortByLikes(@QueryParam("vkPublic") @NotNull String publicVK,
	                                     @QueryParam("offset") Integer lastCount) throws ClientException, ApiException {
		if (publicVK == null || lastCount == null) {
			throw new IllegalArgumentException();
		}

		List<WallpostFull> lastWallPost = _vkApiWorker.getLastWallPost(publicVK, lastCount);

		List<PostsKeeper> collect = lastWallPost.parallelStream()
				.map(wallpostFull -> {
					     List<WallComment> wallComments = _vkApiWorker.getAllWallComments(wallpostFull);
					     wallComments = _vkApiWorker.sortByLikes(wallComments);
					     PostsKeeper.PostsKeeperBuilder builder = new PostsKeeper.PostsKeeperBuilder()
							     .setWallComments(wallComments)
							     .setWallpostFull(wallpostFull);
					return builder.createPostsKeeper();
				     }
				)
				.collect(Collectors.toList());

		return collect;
	}

}
