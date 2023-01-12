/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.roomprofile.polls

import com.airbnb.mvrx.test.MavericksTestRule
import im.vector.app.features.roomprofile.polls.list.domain.GetLoadedPollsStatusUseCase
import im.vector.app.features.roomprofile.polls.list.ui.PollSummary
import im.vector.app.features.roomprofile.polls.list.domain.GetPollsUseCase
import im.vector.app.features.roomprofile.polls.list.domain.LoadMorePollsUseCase
import im.vector.app.features.roomprofile.polls.list.domain.SyncPollsUseCase
import im.vector.app.test.test
import im.vector.app.test.testDispatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

private const val ROOM_ID = "room-id"

class RoomPollsViewModelTest {

    @get:Rule
    val mavericksTestRule = MavericksTestRule(testDispatcher = testDispatcher)

    private val fakeGetPollsUseCase = mockk<GetPollsUseCase>()
    private val fakeGetLoadedPollsStatusUseCase = mockk<GetLoadedPollsStatusUseCase>()
    private val fakeLoadMorePollsUseCase = mockk<LoadMorePollsUseCase>()
    private val fakeSyncPollsUseCase = mockk<SyncPollsUseCase>()
    private val initialState = RoomPollsViewState(ROOM_ID)

    private fun createViewModel(): RoomPollsViewModel {
        return RoomPollsViewModel(
                initialState = initialState,
                getPollsUseCase = fakeGetPollsUseCase,
                getLoadedPollsStatusUseCase = fakeGetLoadedPollsStatusUseCase,
                loadMorePollsUseCase = fakeLoadMorePollsUseCase,
                syncPollsUseCase = fakeSyncPollsUseCase,
        )
    }

    @Test
    fun `given viewModel when created then polls list is observed and viewState is updated`() {
        // Given
        val polls = listOf(givenAPollSummary())
        every { fakeGetPollsUseCase.execute(ROOM_ID) } returns flowOf(polls)
        val expectedViewState = initialState.copy(polls = polls)

        // When
        val viewModel = createViewModel()
        val viewModelTest = viewModel.test()

        // Then
        viewModelTest
                .assertLatestState(expectedViewState)
                .finish()
        verify {
            fakeGetPollsUseCase.execute(ROOM_ID)
        }
    }

    private fun givenAPollSummary(): PollSummary {
        return mockk()
    }
}
