package di

import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import dev.shustoff.dikt.Create
import dev.shustoff.dikt.UseModules
import entity.SingleBookmarkTarget
import entity.error.CompositionLocalError
import kotlinx.coroutines.CoroutineScope
import ui.page.bookmarklist.BookmarkListModel
import ui.page.editor.BookmarkEditorModel
import ui.page.summary.BookmarkSummaryModel
import ui.page.tagedit.TagEditModel
import ui.popup.PopupBaseModel

@Suppress("unused", "UNUSED_PARAMETER")
@UseModules(RepositoryModule::class)
class AppModule(
    val repositoryModule: RepositoryModule
) {

    @Create
    fun createPopupBaseModel(coroutineScope: CoroutineScope): PopupBaseModel

    @Create
    fun createBookmarkEditorModel(
        coroutineScope: CoroutineScope,
        target: SingleBookmarkTarget,
        onNavigateBackState: State<BookmarkEditorModel.OnNavigateBack>
    ): BookmarkEditorModel

    @Create
    fun createBookmarkSummaryModel(
        coroutineScope: CoroutineScope, target: SingleBookmarkTarget
    ): BookmarkSummaryModel

    @Create
    fun createTagEditModel(
        coroutineScope: CoroutineScope,
        onTagEditEventState: State<TagEditModel.OnTagEditEvent>,
    ): TagEditModel

    @Create
    fun createBookmarkListModel(
        coroutineScope: CoroutineScope,
        onBookmarkSelect: State<BookmarkListModel.OnBookmarkSelect>
    ): BookmarkListModel

    companion object {
        val Local = staticCompositionLocalOf<AppModule> {
            throw CompositionLocalError("AppModule")
        }
    }
}