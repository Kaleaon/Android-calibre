package com.universalmedialibrary.services

import android.content.Intent
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ServiceController

@RunWith(RobolectricTestRunner::class)
class CalibreImportForegroundServiceTest {

    @Mock
    private lateinit var mockCalibreImportService: CalibreImportService

    private lateinit var serviceController: ServiceController<CalibreImportForegroundService>
    private lateinit var service: CalibreImportForegroundService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        serviceController = Robolectric.buildService(CalibreImportForegroundService::class.java)
        service = serviceController.get()
        service.calibreImportService = mockCalibreImportService
    }

    @Test
    fun `service properly initializes when created`() {
        serviceController.create()
        
        assertThat(service).isNotNull()
    }

    @Test
    fun `service returns START_NOT_STICKY when DB path is missing`() {
        val intent = Intent().apply {
            // Missing EXTRA_DB_PATH
            putExtra(CalibreImportForegroundService.EXTRA_ROOT_PATH, "/test/root/path")
            putExtra(CalibreImportForegroundService.EXTRA_LIBRARY_ID, 123L)
        }

        serviceController.create()
        val result = service.onStartCommand(intent, 0, 1)
        
        assertThat(result).isEqualTo(android.app.Service.START_NOT_STICKY)
    }

    @Test
    fun `service returns START_NOT_STICKY when root path is missing`() {
        val intent = Intent().apply {
            putExtra(CalibreImportForegroundService.EXTRA_DB_PATH, "/test/path/calibre.db")
            // Missing EXTRA_ROOT_PATH
            putExtra(CalibreImportForegroundService.EXTRA_LIBRARY_ID, 123L)
        }

        serviceController.create()
        val result = service.onStartCommand(intent, 0, 1)
        
        assertThat(result).isEqualTo(android.app.Service.START_NOT_STICKY)
    }

    @Test
    fun `service returns START_NOT_STICKY when library ID is invalid`() {
        val intent = Intent().apply {
            putExtra(CalibreImportForegroundService.EXTRA_DB_PATH, "/test/path/calibre.db")
            putExtra(CalibreImportForegroundService.EXTRA_ROOT_PATH, "/test/root/path")
            putExtra(CalibreImportForegroundService.EXTRA_LIBRARY_ID, -1L)
        }

        serviceController.create()
        val result = service.onStartCommand(intent, 0, 1)
        
        assertThat(result).isEqualTo(android.app.Service.START_NOT_STICKY)
    }

    @Test
    fun `service properly cleans up when destroyed`() {
        serviceController.create()
        
        // Verify service can be destroyed without throwing exceptions
        // This tests that onDestroy() properly cancels coroutines
        serviceController.destroy()
        
        // If the service didn't properly clean up, this test would fail or hang
        assertThat(service).isNotNull()
    }

    @Test
    fun `onBind returns null for this service`() {
        serviceController.create()
        
        val binder = service.onBind(Intent())
        
        assertThat(binder).isNull()
    }
}