package com.brokechef.recipesharingapp.ui.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.enums.FollowModalType
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsCreateRequest
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsFindByUserId200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import com.brokechef.recipesharingapp.data.repository.CollectionsRecipesRepository
import com.brokechef.recipesharingapp.data.repository.CollectionsRepository
import com.brokechef.recipesharingapp.data.repository.FollowsRepository
import com.brokechef.recipesharingapp.data.repository.UploadsRepository
import com.brokechef.recipesharingapp.data.repository.UsersRepository
import com.brokechef.recipesharingapp.ui.components.toast.ToastState
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data class Success(
        val user: UsersFindById200Response,
    ) : ProfileUiState

    data class Error(
        val message: String = "Failed to load profile. Check your connection.",
    ) : ProfileUiState

    data object Loading : ProfileUiState
}

class ProfileViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val usersRepository = UsersRepository(tokenManager)
    private val followsRepository = FollowsRepository(tokenManager)
    private val collectionsRepository = CollectionsRepository(tokenManager)
    private val collectionsRecipesRepository = CollectionsRecipesRepository(tokenManager)

    private val uploadsRepository = UploadsRepository(tokenManager)

    var profileUiState: ProfileUiState by mutableStateOf(ProfileUiState.Loading)
        private set

    var isOwnProfile by mutableStateOf(false)
        private set

    var isFollowing by mutableStateOf(false)
        private set

    var totalFollowers by mutableIntStateOf(0)
        private set

    var totalFollowing by mutableIntStateOf(0)
        private set

    suspend fun loadSavedRecipes(
        userId: String?,
        offset: Int,
        limit: Int,
    ): List<RecipesFindAll200ResponseInner> =
        try {
            val result = usersRepository.getRecipes(offset = offset, limit = limit, id = userId)
            result.saved
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

    suspend fun loadCreatedRecipes(
        userId: String?,
        offset: Int,
        limit: Int,
    ): List<RecipesFindAll200ResponseInner> =
        try {
            val result = usersRepository.getRecipes(offset = offset, limit = limit, id = userId)
            result.created
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

    suspend fun loadTotalSaved(userId: String?): Int =
        try {
            usersRepository.totalSaved(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    suspend fun loadTotalCreated(userId: String?): Int =
        try {
            usersRepository.totalCreated(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    var showFollowModal by mutableStateOf(false)
        private set

    var followModalType by mutableStateOf(FollowModalType.FOLLOWING)
        private set

    var followModalUsers by mutableStateOf<List<UsersFindById200Response>>(emptyList())
        private set

    var isLoadingFollowModalUsers by mutableStateOf(false)
        private set

    // Collections state
    var showCollectionsModal by mutableStateOf(false)
        private set

    var collections by mutableStateOf<List<CollectionsFindByUserId200ResponseInner>>(emptyList())
        private set

    var isLoadingCollections by mutableStateOf(false)
        private set

    var selectedCollection by mutableStateOf<CollectionsFindByUserId200ResponseInner?>(null)
        private set

    var collectionRecipes by mutableStateOf<List<RecipesFindAll200ResponseInner>>(emptyList())
        private set

    var isLoadingCollectionRecipes by mutableStateOf(false)
        private set

    var showCreateCollectionDialog by mutableStateOf(false)
        private set

    var showDeleteCollectionDialog by mutableStateOf(false)
        private set

    var collectionIdToDelete by mutableStateOf<Int?>(null)
        private set

    private var currentUserId: String? = null

    fun loadProfile(userId: String?) {
        currentUserId = userId
        profileUiState = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                val user = usersRepository.findById(userId)
                if (user == null) {
                    profileUiState = ProfileUiState.Error("User not found.")
                    return@launch
                }

                profileUiState = ProfileUiState.Success(user)

                val loggedInUserId = tokenManager.getUserId()
                isOwnProfile = loggedInUserId != null && loggedInUserId == user.id

                launch {
                    totalFollowing = followsRepository.totalFollowing(user.id)
                }
                launch {
                    totalFollowers = followsRepository.totalFollowers(user.id)
                }
                launch {
                    if (!isOwnProfile) {
                        isFollowing = followsRepository.isFollowing(user.id)
                    }
                }
            } catch (e: Exception) {
                profileUiState =
                    ProfileUiState.Error(
                        e.message ?: "Failed to load profile. Check your connection.",
                    )
            }
        }
    }

    fun handleFollow() {
        val state = profileUiState
        if (state !is ProfileUiState.Success) return
        viewModelScope.launch {
            try {
                followsRepository.follow(state.user.id)
                isFollowing = true
                totalFollowers += 1
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleUnfollow() {
        val state = profileUiState
        if (state !is ProfileUiState.Success) return
        viewModelScope.launch {
            try {
                followsRepository.unfollow(state.user.id)
                isFollowing = false
                totalFollowers -= 1
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openFollowModal(type: FollowModalType) {
        val state = profileUiState
        if (state !is ProfileUiState.Success) return

        followModalType = type
        showFollowModal = true
        isLoadingFollowModalUsers = true
        followModalUsers = emptyList()

        viewModelScope.launch {
            try {
                followModalUsers =
                    if (type == FollowModalType.FOLLOWING) {
                        followsRepository.getFollowing(state.user.id)
                    } else {
                        followsRepository.getFollowers(state.user.id)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                followModalUsers = emptyList()
            } finally {
                isLoadingFollowModalUsers = false
            }
        }
    }

    fun closeFollowModal() {
        showFollowModal = false
    }

    fun openCollectionsModal() {
        showCollectionsModal = true
        selectedCollection = null
        collectionRecipes = emptyList()
        fetchCollections()
    }

    fun closeCollectionsModal() {
        showCollectionsModal = false
        selectedCollection = null
    }

    private fun fetchCollections() {
        isLoadingCollections = true
        viewModelScope.launch {
            try {
                collections = collectionsRepository.findByUserId(null)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingCollections = false
            }
        }
    }

    fun openCollectionRecipes(collection: CollectionsFindByUserId200ResponseInner) {
        selectedCollection = collection
        isLoadingCollectionRecipes = true
        viewModelScope.launch {
            try {
                collectionRecipes = collectionsRepository.findRecipesByCollectionId(collection.id)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingCollectionRecipes = false
            }
        }
    }

    fun backToCollections() {
        selectedCollection = null
        collectionRecipes = emptyList()
    }

    fun openDeleteCollectionDialog(collectionId: Int) {
        collectionIdToDelete = collectionId
        showDeleteCollectionDialog = true
    }

    fun dismissDeleteCollectionDialog() {
        showDeleteCollectionDialog = false
        collectionIdToDelete = null
    }

    fun confirmDeleteCollection() {
        val id = collectionIdToDelete ?: return
        showDeleteCollectionDialog = false
        viewModelScope.launch {
            try {
                collectionsRepository.remove(id)
                collections = collections.filter { it.id != id }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            collectionIdToDelete = null
        }
    }

    fun removeRecipeFromCollection(recipeId: Int) {
        val collection = selectedCollection ?: return
        viewModelScope.launch {
            try {
                collectionsRecipesRepository.unsave(
                    collectionId = collection.id,
                    recipeId = recipeId,
                )
                collectionRecipes = collectionRecipes.filter { it.id != recipeId }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openCreateCollectionDialog() {
        showCreateCollectionDialog = true
    }

    fun dismissCreateCollectionDialog() {
        showCreateCollectionDialog = false
    }

    fun createCollection(
        title: String,
        imageBytes: ByteArray? = null,
    ) {
        showCreateCollectionDialog = false
        viewModelScope.launch {
            try {
                ToastState.loading("Creating collection...")
                var imageUrl: String? = null
                if (imageBytes != null) {
                    imageUrl = uploadsRepository.uploadCollectionImage(imageBytes).getOrThrow()
                }
                val created =
                    collectionsRepository.create(
                        CollectionsCreateRequest(title = title, imageUrl = imageUrl),
                    )
                if (created != null) {
                    fetchCollections()
                    ToastState.success("Collection created!")
                } else {
                    ToastState.error("Failed to create collection.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to create collection.")
            }
        }
    }
}
