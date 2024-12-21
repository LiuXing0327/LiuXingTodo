package com.liuxing.todo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.liuxing.todo.utils.VersionUtils.getVersionName
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * Author：流星
 * DateTime：2024/12/21 21:29
 * Description：
 */
object CheckUpdateUtils {

    private const val CHECK_APP_VERSION_URL =
        "https://gitee.com/LiuXing0327/app-version/raw/master/Todo/CheckUpdate/TodoVersion.json"

    /**
     * 检查更新
     *
     * @param context 上下文
     * @param onUpdate 更新回调函数,
     */
    fun checkUpdate(
        context: Context,
        onUpdate: (Boolean, String, String, String) -> Unit
    ) {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(CHECK_APP_VERSION_URL).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onUpdate(false, "", "", "")
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body?.string()
                if (jsonString != null) {
                    val jsonObject = JSONObject(jsonString)
                    val latestVersionCode = jsonObject.getInt("versionCode")
                    val latestVersionName = jsonObject.getString("versionName")
                    val releaseNotes = jsonObject.getString("releaseNotes")
                    val downloadUrl = jsonObject.getString("downloadUrl")
                    val currentVersionCode = VersionUtils.getVersionCode(context)

                    // 最新版本大于当前版本
                    if (latestVersionCode > currentVersionCode) {
                        onUpdate(true, latestVersionName, releaseNotes, downloadUrl)
                    } else {
                        onUpdate(false, "", "", "")
                    }
                }
            }
        })
    }
}

@Composable
fun CheckUpdateDialog() {
    var showDialog by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }
    var versionInfo by remember { mutableStateOf(VersionInfo("", "", "", "")) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!isChecking) {
            isChecking = true

            CheckUpdateUtils.checkUpdate(context) { updateAvailable, latestVersionName, releaseNotes, downloadUrl ->
                if (updateAvailable) {
                    versionInfo = VersionInfo(latestVersionName, releaseNotes, downloadUrl,
                        getVersionName(context)!!
                    )
                    showDialog = true
                }
                isChecking = false
            }
        }
    }
    AboutAlertDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        versionInfo = versionInfo,
        context = context
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAlertDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    versionInfo: VersionInfo,
    context: Context
) {
    if (showDialog) {
        BasicAlertDialog(onDismissRequest = onDismissRequest) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "About Icon",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "版本更新",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "新版本：${versionInfo.latestVersionName}\n\n" +
                                "更新内容：\n${versionInfo.releaseNotes}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onDismissRequest()
                            }, modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(text = "取消")
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.downloadUrl))
                                context.startActivity(intent)
                                onDismissRequest()
                            }
                        ) {
                            Text(text = "确定")
                        }
                    }
                }
            }
        }
    }
}

data class VersionInfo(
    val latestVersionName: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val currentVersionName: String
)