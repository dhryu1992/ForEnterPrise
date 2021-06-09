package com.awesome.appstore.activity

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.appstore.R
import com.awesome.appstore.activity.adapter.AppImgAdapter
import com.awesome.appstore.activity.adapter.ReviewAdapter
import com.awesome.appstore.activity.viewmodel.DetailActivityViewModel
import com.awesome.appstore.config.*
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_IMAGE_INDEX
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_IMAGE_URL
import com.awesome.appstore.config.StringTAG.Companion.EXTRA_KEY_APP_INFO
import com.awesome.appstore.database.AppInfo
import com.awesome.appstore.databinding.ActivityDetailBinding
import com.awesome.appstore.databinding.DialogReviewBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.awesome.appstore.protocol.response.Review
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.awesome.appstore.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.dialog_review.view.*
import kotlinx.coroutines.*
import javax.inject.Inject

class DetailActivity : BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var packageUtil: PackageUtil

    @Inject
    lateinit var preference: StorePreference

    lateinit var viewModel: DetailActivityViewModel
    lateinit var binding: ActivityDetailBinding
    lateinit var appInfo: AppInfo
    lateinit var receiver: BroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appInfo = intent.getParcelableExtra(EXTRA_KEY_APP_INFO)!!
        Logger.d(appInfo)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(DetailActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        appInfo.let { viewModel.setAppInfo(it) }

        Glide.with(this)
            .load(appInfo.iconUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(Utils().dpToPx(20))))
            .into(binding.imageAppThum)

        viewModel.onClickExit.observe(this, {
            finish()
        })
        viewModel.appDownloadComplete.observe(this, {
            installApp()
        })
        viewModel.appDetailImgUrl.observe(this, { urlList ->
            binding.recyclerAppImgList.adapter = AppImgAdapter { url, index ->
                Intent(this, FullSizeImageActivity::class.java)
                    .apply { putExtra(EXTRA_IMAGE_URL, url) }
                    .apply { putExtra(EXTRA_IMAGE_INDEX, index) }
                    .also { startActivity(it) }
            }.apply {
                items =
                    urlList as ArrayList<String>
            }
            binding.recyclerAppImgList.layoutManager =
                LinearLayoutManager(this).also { it.orientation = RecyclerView.HORIZONTAL }
            binding.recyclerAppImgList.visibility = View.VISIBLE
            onRestart()
        })

        viewModel.appStatusUpdateComplete.observe(this, {
            viewModel.AppRefresh()
            Logger.d("refresh broadcast")
            Intent().also { intent ->
                intent.action = StringTAG.ACTION_APP_LIST_REFRESH
                intent.setPackage(this.packageName)
                this.sendBroadcast(intent)
            }
        })

        viewModel.appDelete.observe(this, {
            val packageUri = Uri.parse("package:${appInfo.packageName}")
            val deleteIntent = Intent(Intent.ACTION_DELETE, packageUri)
            startActivity(deleteIntent)
        })

        viewModel.appExecute.observe(this, {
            if (TabActivity.badgeEssential <= 0) {
                if (packageUtil.getPackageNameHashString(appInfo.packageName) == appInfo.checksum) {
                    viewModel.pushCountReset()
                    packageUtil.startApplication(appInfo.packageName)
                    Logger.d("${appInfo.appName} run")
                } else {
                    Snackbar.make(binding.root, "앱 위변조 검사 통과 실패!!", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                this.finish()
                Intent().also { intent ->
                    intent.action = StringTAG.ACTION_ESSENTIAL_APP_EXIST
                    intent.setPackage(packageName)
                    this.sendBroadcast(intent)
                }
            }
        })

        viewModel.appUpdate.observe(this, {
            installApp()
        })

        viewModel.onClickEvaluation.observe(this, {
            val view = layoutInflater.inflate(R.layout.dialog_review, null)
            val dialogBinding = DialogReviewBinding.bind(view)

            val dialog = MaterialAlertDialogBuilder(this)
                .setView(view)
                .show()

            dialogBinding.textReviewSubmit.setOnClickListener {
                viewModel.writeReview(
                    dialogBinding.editReviewTitle.text.toString(),
                    dialogBinding.editReviewContent.text.toString(),
                    dialogBinding.ratingDialog.rating
                )
                Logger.d("title : ${dialogBinding.editReviewTitle.text}, content : ${dialogBinding.editReviewContent.text}, rating: ${dialogBinding.ratingDialog.rating}")
                dialog.cancel()
            }
            dialogBinding.textReviewCancel.setOnClickListener {
                dialog.cancel()
            }
        })

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Logger.d("onReceive: $intent")
                var type: String = ""
                when (intent?.action) {
                    Intent.ACTION_PACKAGE_REMOVED -> {
                        type = "D"
                    }
                    Intent.ACTION_PACKAGE_ADDED -> {
                        val replaced = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        if (replaced) {
                            Logger.d("Replaced ? $replaced")
                            type = "U"
                        } else {
                            type = "I"
                        }
                    }
                }
                viewModel.appStatusUpdate(type)
            }
        }
        registerReceiver(receiver, intentFilter)

        viewModel.reviewList.observe(this, {
            Logger.d("reviewList.observe")
            Logger.d(it)
            if (viewModel.pagingInfo.value?.page!! <= 1) {
                (binding.recyclerReviewList.adapter as ReviewAdapter).items = it as ArrayList<Review?>
                binding.recyclerReviewList.adapter?.notifyDataSetChanged()
            } else {
                val size = (binding.recyclerReviewList.adapter as ReviewAdapter).items.size
                (binding.recyclerReviewList.adapter as ReviewAdapter).items.plusAssign(it as ArrayList<Review?>)
                (binding.recyclerReviewList.adapter as ReviewAdapter).notifyItemRangeInserted(size, it.size)
                binding.progressReview.visibility = View.GONE
            }
        })
        binding.recyclerReviewList.adapter = ReviewAdapter(preference.loadIdSavePreference()!!,{
            val view = layoutInflater.inflate(R.layout.dialog_review, null)
            val dialogBinding = DialogReviewBinding.bind(view)
            dialogBinding.ratingDialog.rating = viewModel.myReview.starPoint
            dialogBinding.editReviewTitle.setText(viewModel.myReview.subject)
            dialogBinding.editReviewContent.setText(viewModel.myReview.content)
            dialogBinding.textReviewSubmit.text = "수정"

            val dialog = MaterialAlertDialogBuilder(this)
                .setView(view)
                .show()

            dialogBinding.textReviewSubmit.setOnClickListener {
                viewModel.editReview(
                    dialogBinding.editReviewTitle.text.toString(),
                    dialogBinding.editReviewContent.text.toString(),
                    dialogBinding.ratingDialog.rating
                )
                Logger.d("title : ${dialogBinding.editReviewTitle.text}, content : ${dialogBinding.editReviewContent.text}, rating: ${dialogBinding.ratingDialog.rating}")
                dialog.cancel()
            }

            dialogBinding.textReviewCancel.setOnClickListener {
                dialog.cancel()
            }
        },{
            MaterialAlertDialogBuilder(this)
                .setMessage("리뷰를 삭제 하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    viewModel.deleteReview()
                }
                .setNegativeButton("취소") { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                .show()
        })
        binding.recyclerReviewList.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
        binding.recyclerReviewList.visibility = View.VISIBLE

        binding.recyclerReviewList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastViewPosition = (binding.recyclerReviewList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                Logger.d("lastViewPosition : $lastViewPosition")
                Logger.d("itemCount ${(binding.recyclerReviewList.adapter as ReviewAdapter).itemCount}")
                if ((binding.recyclerReviewList.adapter as ReviewAdapter).itemCount == lastViewPosition + 1) {
                    Logger.d("lastViewPosition")
                    if (viewModel.pagingInfo.value?.page!! < viewModel.pagingInfo.value?.totalPage!!) {
                        binding.progressReview.visibility = View.VISIBLE
                        viewModel.getReviewList(viewModel.pagingInfo.value?.page!! + 1)
                    }
//                        binding.recyclerReviewList.removeOnScrollListener(this)
                }
            }
        })
//        viewModel.reviewUpdateDialog.observe(this, {
//            val view = layoutInflater.inflate(R.layout.dialog_review, null)
//            val dialogBinding = DialogReviewBinding.bind(view)
//            dialogBinding.ratingDialog.rating = viewModel.myReview.starPoint
//            dialogBinding.editReviewTitle.setText(viewModel.myReview.subject)
//            dialogBinding.editReviewContent.setText(viewModel.myReview.content)
//            dialogBinding.textReviewSubmit.text = "수정"
//
//            val dialog = MaterialAlertDialogBuilder(this)
//                .setView(view)
//                .show()
//
//            dialogBinding.textReviewSubmit.setOnClickListener {
//                viewModel.editReview(
//                    dialogBinding.editReviewTitle.text.toString(),
//                    dialogBinding.editReviewContent.text.toString(),
//                    dialogBinding.ratingDialog.rating
//                )
//                Logger.d("title : ${dialogBinding.editReviewTitle.text}, content : ${dialogBinding.editReviewContent.text}, rating: ${dialogBinding.ratingDialog.rating}")
//                dialog.cancel()
//            }
//
//            dialogBinding.textReviewCancel.setOnClickListener {
//                dialog.cancel()
//            }
//        })
//
//        viewModel.reviewDeleteDialog.observe(this, {
//            MaterialAlertDialogBuilder(this)
//                .setMessage("리뷰를 삭제 하시겠습니까?")
//                .setPositiveButton("삭제") { _, _ ->
//                    viewModel.deleteReview()
//                }
//                .setNegativeButton("취소") { dialogInterface, _ ->
//                    dialogInterface.cancel()
//                }
//                .show()
//        })

        viewModel.tokenExpireDialog.observe(this, {
            tokenExpireDialog()
        })

        viewModel.toastMsg.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == StoreConst.REQUEST_INSTALL_PERMISSION) {
            packageUtil.startInstallPackage(
                this,
                "/${appInfo.packageName}.apk",
                1000,
                true
            )
        }
    }

    fun installApp() {
        if (packageManager.canRequestPackageInstalls()) {
            Logger.d("canRequestPackageInstalls")
            packageUtil.startInstallPackage(this, "/${appInfo.packageName}.apk", 1000, true)
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${this.packageName}")
            )
            startActivityForResult(intent, StoreConst.REQUEST_INSTALL_PERMISSION)
            Toast.makeText(this, "설치권한 없음", Toast.LENGTH_SHORT).show()
        }
    }
}