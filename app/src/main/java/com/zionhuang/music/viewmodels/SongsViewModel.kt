package com.zionhuang.music.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.zionhuang.innertube.utils.plus
import com.zionhuang.music.db.entities.LocalItem
import com.zionhuang.music.db.entities.SongHeader
import com.zionhuang.music.models.PreferenceSortInfo
import com.zionhuang.music.models.base.IMutableSortInfo
import com.zionhuang.music.playback.MediaSessionConnection
import com.zionhuang.music.repos.SongRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@Suppress("UNCHECKED_CAST")
class SongsViewModel(application: Application) : AndroidViewModel(application) {
    val songRepository = SongRepository
    val mediaSessionConnection = MediaSessionConnection

    val sortInfo: IMutableSortInfo = PreferenceSortInfo

    var query: String? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val allSongsFlow = PreferenceSortInfo.liveData.asFlow().flatMapLatest { sortInfo ->
        SongRepository.getAllSongs(sortInfo).flow
    }.map { list ->
        SongHeader(SongRepository.getSongCount(), PreferenceSortInfo) + list
    }

    val allArtistsFlow: Flow<PagingData<LocalItem>> by lazy {
        Pager(PagingConfig(pageSize = 50)) {
            songRepository.getAllArtists().pagingSource as PagingSource<Int, LocalItem>
        }.flow.cachedIn(viewModelScope)
    }

    val allAlbumsFlow: Flow<PagingData<LocalItem>> by lazy {
        Pager(PagingConfig(pageSize = 50)) {
            songRepository.getAllAlbums().pagingSource as PagingSource<Int, LocalItem>
        }.flow.cachedIn(viewModelScope)
    }

    val allPlaylistsFlow: Flow<PagingData<LocalItem>> by lazy {
        Pager(PagingConfig(pageSize = 50)) {
            songRepository.getAllPlaylists().pagingSource as PagingSource<Int, LocalItem>
        }.flow.cachedIn(viewModelScope)
    }

    fun getArtistSongsAsFlow(artistId: String) = Pager(PagingConfig(pageSize = 50)) {
        songRepository.getArtistSongs(artistId, sortInfo).pagingSource as PagingSource<Int, LocalItem>
    }.flow.cachedIn(viewModelScope)

    fun getPlaylistSongsAsFlow(playlistId: String) = Pager(PagingConfig(pageSize = 50)) {
        songRepository.getPlaylistSongs(playlistId, sortInfo).pagingSource
    }.flow.cachedIn(viewModelScope)
}
