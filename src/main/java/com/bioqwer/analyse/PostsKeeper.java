package com.bioqwer.analyse;

import java.util.List;
import java.util.Objects;

import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.WallpostFull;

public class PostsKeeper {

	private WallpostFull _wallpostFull;
	private List<WallComment> _wallComments;

	public PostsKeeper(WallpostFull wallpostFull,
	                   List<WallComment> wallComments) {
		_wallpostFull = wallpostFull;
		_wallComments = wallComments;
	}

	public WallpostFull getWallpostFull() {
		return _wallpostFull;
	}

	public void setWallpostFull(WallpostFull wallpostFull) {
		_wallpostFull = wallpostFull;
	}

	public List<WallComment> getWallComments() {
		return _wallComments;
	}

	public void setWallComments(List<WallComment> wallComments) {
		_wallComments = wallComments;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PostsKeeper)) return false;
		PostsKeeper that = (PostsKeeper) o;
		return Objects.equals(_wallpostFull, that._wallpostFull) &&
				Objects.equals(_wallComments, that._wallComments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_wallpostFull, _wallComments);
	}


	@Override
	public String toString() {
		return "PostsKeeper{" +
				"_wallpostFull=" + _wallpostFull +
				", _wallComments=" + _wallComments +
				'}';
	}

	public static class PostsKeeperBuilder {
		private WallpostFull _wallpostFull;
		private List<WallComment> _wallComments;

		public PostsKeeperBuilder setWallpostFull(WallpostFull wallpostFull) {
			_wallpostFull = wallpostFull;
			return this;
		}

		public PostsKeeperBuilder setWallComments(List<WallComment> wallComments) {
			_wallComments = wallComments;
			return this;
		}

		public PostsKeeper createPostsKeeper() {
			return new PostsKeeper(_wallpostFull, _wallComments);
		}
	}
}
