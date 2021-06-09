package com.awesome.appstore.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.*
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.awesome.appstore.BuildConfig
import com.awesome.appstore.R
import com.awesome.appstore.activity.adapter.SectionsPagerAdapter
import com.awesome.appstore.activity.fragment.SearchFragment
import com.awesome.appstore.activity.viewmodel.TabActivityViewModel
import com.awesome.appstore.config.StoreConfig.Companion.MDM_ENABLE
import com.awesome.appstore.config.StoreConst
import com.awesome.appstore.config.StringTAG
import com.awesome.appstore.databinding.ActivityTabBinding
import com.awesome.appstore.module.viewmodels.ViewModelFactory
import com.awesome.appstore.security.mdm.MDMHelper
import com.awesome.appstore.util.PackageUtil
import com.awesome.appstore.util.StorePreference
import com.awesomebly.push.lib.pushlistener.core.PushManager
import com.bumptech.glide.Glide
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_tab.*
import kotlinx.android.synthetic.main.layout_side_navigation.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.exitProcess


class TabActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var preferences: StorePreference

    @Inject
    lateinit var packageUtil: PackageUtil

    @Inject
    lateinit var mdmHelper: MDMHelper

    private lateinit var binding: ActivityTabBinding

    private lateinit var viewModel: TabActivityViewModel

    val searchFragment = SearchFragment()

    private var currentAnimator: Animator? = null
    private var shortAnimationDuration: Int = 0

    companion object {
        var badgeExecute = 0
        var badgeAll = 0
        var badgeEssential = 0
        var badgeUpdate = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tab)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TabActivityViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.layoutSideNavigation.viewModel = viewModel

        val metrics = resources.displayMetrics
        val widthPixels = metrics.widthPixels * 6 / 10

        binding.layoutSideNavigation.width = widthPixels

        binding.viewPager.adapter = SectionsPagerAdapter(this, supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.viewPager)

//       푸시 등록
        PushManager(applicationContext).register("V0179eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE2MTMwMTA2MzUsImV4cCI6MTYzOTE0ODQwMH0.SyejYGM2839zflpTEccLemT5cLcEujsaCKkRTjXYx36JeQA-Xb1FCpQ49yB8-5oc2GaG9nGK6Xx65eTUSyjRFA")

        val badgeExecuteTab = tabs.getTabAt(0)?.orCreateBadge
            ?.apply {
                badgeGravity = BadgeDrawable.TOP_END
                backgroundColor = ContextCompat.getColor(this@TabActivity, R.color.badge_background)
                number = 0
                if (number == 0) this.isVisible = false
            }


        val badgeAllTab = tabs.getTabAt(1)?.orCreateBadge
            ?.apply {
                badgeGravity = BadgeDrawable.TOP_END
                backgroundColor = ContextCompat.getColor(this@TabActivity, R.color.badge_background)
                number = viewModel.badge2.value ?: 0
                if (number == 0) this.isVisible = false
            }


        val badgeEssentialTab = tabs.getTabAt(2)?.orCreateBadge
            ?.apply {
                badgeGravity = BadgeDrawable.TOP_END
                backgroundColor = ContextCompat.getColor(this@TabActivity, R.color.badge_background)
                number = viewModel.badge3.value ?: 0
                if (number == 0) this.isVisible = false
            }


        val badgeUpdateTab = tabs.getTabAt(3)?.orCreateBadge
            ?.apply {
                badgeGravity = BadgeDrawable.TOP_END
                backgroundColor = ContextCompat.getColor(this@TabActivity, R.color.badge_background)
                number = viewModel.badge4.value ?: 0
                if (number == 0) this.isVisible = false
            }

        viewModel.badge1.observe(this, {
            badgeExecute = it
            badgeExecuteTab?.number = it
            badgeExecuteTab?.isVisible = it != 0
        })
        viewModel.badge2.observe(this, {
            badgeAll = it
            Logger.d(it)
            badgeAllTab?.number = it
            badgeAllTab?.isVisible = it != 0
        })
        viewModel.badge3.observe(this, {
            badgeEssential = it
            badgeEssentialTab?.number = it
            badgeEssentialTab?.isVisible = it != 0
            if (it > 0) {
                binding.tabs.selectTab(binding.tabs.getTabAt(2))
                Snackbar.make(binding.root, "필수 설치 앱이 있습니다.", Snackbar.LENGTH_SHORT).show()
            }
        })
        viewModel.badge4.observe(this, {
            badgeUpdate = it
            badgeUpdateTab?.number = it
            badgeUpdateTab?.isVisible = it != 0
        })

        viewModel.sideNavOpen.observe(this, {
            if (it.getContentIfNotHandled()!!) {
                binding.drawerLayout.openDrawer(Gravity.LEFT)
                binding.searchView.clearFocus()
            }
        }
        )

        viewModel.goNotice.observe(this, {
            if (it.getContentIfNotHandled()!!) {
                startActivity(Intent(this, NoticeActivity::class.java))
            }
        })
        val intentFilter = IntentFilter()
        intentFilter.addAction(StringTAG.ACTION_APP_LIST_REFRESH)
        intentFilter.addAction(StringTAG.ACTION_ESSENTIAL_APP_EXIST)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    StringTAG.ACTION_ESSENTIAL_APP_EXIST -> {
                        binding.tabs.selectTab(binding.tabs.getTabAt(2))
                        Snackbar.make(binding.root, "필수 설치 앱이 있습니다.", Snackbar.LENGTH_SHORT).show()
                    }
                    StringTAG.ACTION_APP_LIST_REFRESH -> {
                        viewModel.badgeRefresh()
                    }
                }
            }
        }
        registerReceiver(receiver, intentFilter)

        binding.searchView.setOnSearchClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Logger.d("onExpend")
                binding.tabs.visibility = View.GONE
                binding.viewPager.visibility = View.GONE
                binding.textAppBarTitle.visibility = View.GONE
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container_search, searchFragment)
                    .commit()
            }
        })
        binding.searchView.setOnCloseListener(object : androidx.appcompat.widget.SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                Logger.d("onClose")
                binding.tabs.visibility = View.VISIBLE
                binding.viewPager.visibility = View.VISIBLE
                binding.textAppBarTitle.visibility = View.VISIBLE
                supportFragmentManager
                    .beginTransaction()
                    .remove(searchFragment)
                    .commit()
                return false
            }
        })
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Logger.d("onQueryTextSubmit")
                if (query == "dev_permission") {
                    Logger.d("error log manage")
                    startActivity(Intent(this@TabActivity, ErrorLogActivity::class.java))
                } else {
                    val result = viewModel.allApplist.value?.filter { it?.appName?.contains(query as CharSequence, true) == true }
                    searchFragment.setSearchData(result)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Logger.d("onQueryTextChange")
                val result = viewModel.allApplist.value?.filter { it?.appName?.contains(newText as CharSequence, true) == true }
                Logger.d(result)
                if (result != null) searchFragment.setSearchData(result)
                return false
            }
        })
        viewModel.goSetting.observe(this, {

            startActivity(Intent(this@TabActivity, SettingActivity::class.java))
        })
        viewModel.goLogout.observe(this, {
            MaterialAlertDialogBuilder(this)
                .setCancelable(true)
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("확인", DialogInterface.OnClickListener { i, _ ->
                    i.dismiss()
                    startActivity(Intent(this@TabActivity, LoginActivity::class.java))
                    finish()
                })
                .setNegativeButton("취소") { i, _ ->
                    i.dismiss()
                }
                .show()

        })
        viewModel.tokenExpireDialog.observe(this, {
            tokenExpireDialog()
        })
        viewModel.userProfile.observe(this, {
            if (it.profileImg.isNotEmpty()) {
                Glide.with(this)
                    .load(it.profileImg)
                    .circleCrop()
                    .into(binding.layoutSideNavigation.imageUserProfile)
            }
            binding.layoutSideNavigation.textUserName.text = it.name
            binding.layoutSideNavigation.textUserDepartment.text = it.departmentName
        })
        viewModel.title.observe(this, {
            binding.textAppBarTitle.text = it
        })
        viewModel.storeInfo.observe(this, {
            (it["versionName"] as CharSequence).let { binding.layoutSideNavigation.textStoreVersion.text = it }
            var isStoreUpdate: Boolean
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                isStoreUpdate = it["versionCode"]?.toString()?.toLong()!! > packageUtil.getInstalledPackageInfo(packageName)?.longVersionCode!!
            } else {
                isStoreUpdate = it["versionCode"]?.toString()?.toLong()!! > packageUtil.getInstalledPackageInfo(packageName)?.versionCode!!
            }
            if (isStoreUpdate) {
                MaterialAlertDialogBuilder(this)
                    .setCancelable(false)
                    .setMessage("앱스토어 최신 버전이 존재합니다. 업데이트를 진행합니다.")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { i, _ ->
                        i.dismiss()
                        MaterialAlertDialogBuilder(this)
                            .setView(R.layout.dialog_store_update)
                            .setCancelable(false)
                            .show()
                        viewModel.storeUpdate()
                    })
                    .show()
            } else if (packageUtil.getPackageNameHashString(packageName) != it["checksum"]) {
                Logger.e("application has been falsified.\nhash = ${packageUtil.getPackageNameHashString(packageName)} checksum = ${it["checksum"]}")
                if (!BuildConfig.DEBUG) {
                    Toast.makeText(applicationContext, "스토어앱이 위변조 되었습니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show()
                    GlobalScope.launch {
                        delay(2000)
                        exitProcess(0)
                    }
                }
            }
            binding.layoutSideNavigation.textStoreVersion.text = it["versionName"].toString()
        })
        viewModel.storeDownloadComplete.observe(this, {
            Logger.d("store apk downLoad complete")
            if (packageManager.canRequestPackageInstalls()) {
                Logger.d("canRequestPackageInstalls")
                packageUtil.startInstallPackage(
                    this@TabActivity,
                    "/$packageName.apk",
                    1000,
                    true
                )
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${packageName}")
                )
                startActivityForResult(intent, StoreConst.REQUEST_INSTALL_PERMISSION)
                Toast.makeText(this@TabActivity, "설치권한 없음", Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.toastMsg.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })



//        binding.layoutSideNavigation.imageUserProfile.setOnClickListener {
//                zoomImageFromThumb(it)
//        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

    }

    override fun onRestart() {
        super.onRestart()
        viewModel.getSettings()
    }

    override fun onPause() {
        binding.drawerLayout.closeDrawer(Gravity.LEFT)
        super.onPause()
    }

    override fun onBackPressed() {
        Logger.d(binding.searchView.isIconified)
        if (!binding.searchView.isIconified) {
            binding.searchView.isIconified = true
            binding.searchView.onActionViewCollapsed()
            binding.tabs.visibility = View.VISIBLE
            binding.viewPager.visibility = View.VISIBLE
            binding.textAppBarTitle.visibility = View.VISIBLE
            supportFragmentManager
                .beginTransaction()
                .remove(searchFragment)
                .commit()
        } else {
            MaterialAlertDialogBuilder(this)
                .setMessage("종료 하시겠습니까?")
                .setPositiveButton("종료", DialogInterface.OnClickListener { _, _ ->
                    preferences.saveIdSavePreference("")
                    preferences.saveTokenPreference("")
                    exitProcess(0)
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, _ -> dialogInterface.cancel() })
                .show()
        }
    }

    override fun onDestroy() {
        if (MDM_ENABLE) {
            mdmHelper.checkMdmStatus(applicationContext, this)
            mdmHelper.stopMDM()
            unbindService(mdmHelper.mdmServiceConn)
        }
        super.onDestroy()
    }

//    private fun zoomImageFromThumb(thumbView: View) {
//        // If there's an animation in progress, cancel it
//        // immediately and proceed with this one.
//
//        currentAnimator?.cancel()
//
//        // Load the high-resolution "zoomed-in" image.
//
////        val expandedImageView: ImageView = findViewById(R.id.expanded_image)
////        binding.imageProfileZoom.setImageResource(imageResId)
//        Glide.with(this)
//            .load(viewModel.userProfile.value?.profileImg)
//            .circleCrop()
//            .into(binding.layoutSideNavigation.imageProfileZoom)
//
//
//        // Calculate the starting and ending bounds for the zoomed-in image.
//        // This step involves lots of math. Yay, math.
//        val startBoundsInt = Rect()
//        val finalBoundsInt = Rect()
//        val globalOffset = Point()
//
//        // The start bounds are the global visible rectangle of the thumbnail,
//        // and the final bounds are the global visible rectangle of the container
//        // view. Also set the container view's offset as the origin for the
//        // bounds, since that's the origin for the positioning animation
//        // properties (X, Y).
//        thumbView.getGlobalVisibleRect(startBoundsInt)
//        binding.layoutSideNavigation.imageProfileZoom
//            .getGlobalVisibleRect(finalBoundsInt, globalOffset)
//        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
//        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)
//
//        val startBounds = RectF(startBoundsInt)
//        val finalBounds = RectF(finalBoundsInt)
//
//        // Adjust the start bounds to be the same aspect ratio as the final
//        // bounds using the "center crop" technique. This prevents undesirable
//        // stretching during the animation. Also calculate the start scaling
//        // factor (the end scaling factor is always 1.0).
//        val startScale: Float
//        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
//            // Extend start bounds horizontally
//            startScale = startBounds.height() / finalBounds.height()
//            val startWidth: Float = startScale * finalBounds.width()
//            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
//            startBounds.left -= deltaWidth.toInt()
//            startBounds.right += deltaWidth.toInt()
//        } else {
//            // Extend start bounds vertically
//            startScale = startBounds.width() / finalBounds.width()
//            val startHeight: Float = startScale * finalBounds.height()
//            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
//            startBounds.top -= deltaHeight.toInt()
//            startBounds.bottom += deltaHeight.toInt()
//        }
//
//        // Hide the thumbnail and show the zoomed-in view. When the animation
//        // begins, it will position the zoomed-in view in the place of the
//        // thumbnail.
//        thumbView.alpha = 0f
//        binding.layoutSideNavigation.imageProfileZoom.visibility = View.VISIBLE
//
//        // Set the pivot point for SCALE_X and SCALE_Y transformations
//        // to the top-left corner of the zoomed-in view (the default
//        // is the center of the view).
//        binding.layoutSideNavigation.imageProfileZoom.pivotX = 0f
//        binding.layoutSideNavigation.imageProfileZoom.pivotY = 0f
//
//        // Construct and run the parallel animation of the four translation and
//        // scale properties (X, Y, SCALE_X, and SCALE_Y).
//        currentAnimator = AnimatorSet().apply {
//            play(ObjectAnimator.ofFloat(
//                binding.layoutSideNavigation.imageProfileZoom,
//                View.X,
//                startBounds.left,
//                finalBounds.left)
//            ).apply {
//                with(ObjectAnimator.ofFloat(binding.layoutSideNavigation.imageProfileZoom, View.Y, startBounds.top, finalBounds.top))
//                with(ObjectAnimator.ofFloat(binding.layoutSideNavigation.imageProfileZoom, View.SCALE_X, startScale, 1f))
//                with(ObjectAnimator.ofFloat(binding.layoutSideNavigation.imageProfileZoom, View.SCALE_Y, startScale, 1f))
//            }
//            duration = shortAnimationDuration.toLong()
//            interpolator = DecelerateInterpolator()
//            addListener(object : AnimatorListenerAdapter() {
//
//                override fun onAnimationEnd(animation: Animator) {
//                    currentAnimator = null
//                }
//
//                override fun onAnimationCancel(animation: Animator) {
//                    currentAnimator = null
//                }
//            })
//            start()
//        }
//
//        // Upon clicking the zoomed-in image, it should zoom back down
//        // to the original bounds and show the thumbnail instead of
//        // the expanded image.
//        binding.layoutSideNavigation.imageProfileZoom.setOnClickListener {
//            currentAnimator?.cancel()
//
//            // Animate the four positioning/sizing properties in parallel,
//            // back to their original values.
//            currentAnimator = AnimatorSet().apply {
//                play(ObjectAnimator.ofFloat(binding.layoutSideNavigation.imageProfileZoom, View.X, startBounds.left)).apply {
//                    with(ObjectAnimator.ofFloat(binding.layoutSideNavigation.imageProfileZoom, View.Y, startBounds.top))
//                    with(ObjectAnimator.ofFloat(binding.layoutSideNavigation.imageProfileZoom, View.SCALE_X, startScale))
//                    with(ObjectAnimator.ofFloat(binding.layoutSideNavigation.imageProfileZoom, View.SCALE_Y, startScale))
//                }
//                duration = shortAnimationDuration.toLong()
//                interpolator = DecelerateInterpolator()
//                addListener(object : AnimatorListenerAdapter() {
//
//                    override fun onAnimationEnd(animation: Animator) {
//                        thumbView.alpha = 1f
//                        binding.layoutSideNavigation.imageProfileZoom.visibility = View.GONE
//                        currentAnimator = null
//                    }
//
//                    override fun onAnimationCancel(animation: Animator) {
//                        thumbView.alpha = 1f
//                        binding.layoutSideNavigation.imageProfileZoom.visibility = View.GONE
//                        currentAnimator = null
//                    }
//                })
//                start()
//            }
//        }
//    }
}


@BindingAdapter("android:layout_width")
fun setWidth(view: View, width: Int) {
    view.layoutParams.apply {
        this.width = width
    }
}

@BindingAdapter("android:layout_height")
fun setHeight(view: View, height: Int) {
    view.layoutParams.apply {
        this.height = height
    }
}