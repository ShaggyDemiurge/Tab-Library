package data

import browser.tabs.CreateCreateProperties
import browser.tabs.QueryQueryInfo
import browser.tabs.Tab
import browser.windows.QueryOptions
import browser.windows.WindowType
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import org.w3c.dom.BroadcastChannel

class PolyfillBrowserInteractor : BrowserInteractor {

    override suspend fun getCurrentTab() = browser.tabs.query(QueryQueryInfo {
        active = true
        currentWindow = true
    }).await().first()

    override fun openManager() {
        browser.tabs.create(CreateCreateProperties {
            this.url = "manager.html"
        })
    }

    override fun openPage(url: String) {
        browser.tabs.create(CreateCreateProperties {
            this.url = url
            this.active = true
        })
    }

    override fun openPages(urls: List<String>) {
        val confirm = window.confirm("You're going to open ${urls.size} tabs")
        if (confirm) {
            urls.forEach { openPage(it) }
        }
    }

    private val updateChannel = BroadcastChannel("bookmark_db_update")
    private val localUpdateFlow = MutableSharedFlow<String>()

    override suspend fun sendUpdateMessage(url: String) {
        updateChannel.postMessage(url)
        localUpdateFlow.emit(url)
    }

    override fun subscribeToDbUpdates(): Flow<String> =
        merge(
            callbackFlow {
                updateChannel.onmessage = {
                    trySend(it.data as String)
                }
                awaitClose {
                    updateChannel.onmessage = null
                }
            },
            localUpdateFlow
        )

    override suspend fun getWindowIds(): List<Int> =
        browser.windows.getAll(QueryOptions {
            populate = false
            windowTypes = arrayOf(WindowType.normal)
        }).await().mapNotNull { it.id }

    override suspend fun getWindowTabs(windowId: Int): List<Tab> =
        browser.tabs.query(
            QueryQueryInfo {
                this.windowId = windowId
            }
        ).await().toList()

    override suspend fun getCurrentWindowId(): Int? =
        browser.windows.getCurrent().await().id
}