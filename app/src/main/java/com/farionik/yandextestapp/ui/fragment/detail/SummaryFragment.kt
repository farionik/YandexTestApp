package com.farionik.yandextestapp.ui.fragment.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentSummaryBinding
import com.farionik.yandextestapp.ui.fragment.BaseFragment
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class SummaryFragment : BaseFragment() {

    private lateinit var binding: FragmentSummaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        companyViewModel.companyLoadingState.observe(viewLifecycleOwner, {
            it?.let {
                binding.progressBar.isVisible = it.state == WorkInfo.State.RUNNING
            }
        })

        companyViewModel.selectedStock.observe(viewLifecycleOwner, {
            binding.run {
                it.logo?.let { logo -> loadLogo(logo.localPath) }
            }
        })

        companyViewModel.selectedCompany.observe(viewLifecycleOwner, {
            it?.let {
                binding.run {
                    tvSymbol.text = it.symbol
                    tvCompanyName.text = it.companyName
                    tvDescription.text = it.description
                    tvCeo.text = it.CEO

                    createWebsiteClickableSpan(it.website)
                    it.phone?.let { phone ->
                        tvPhone.text = PhoneNumberUtils.formatNumber(phone, "US")
                    }

                    val companyAddress = StringBuilder().apply {
                        fun checkAndAppendAddress(address: String?) {
                            address?.let {
                                if (this.isNotBlank() and this.isNotEmpty()) append(", ")
                                append(address)
                            }
                        }

                        it.run {
                            checkAndAppendAddress(country)
                            checkAndAppendAddress(state)
                            checkAndAppendAddress(city)
                            checkAndAppendAddress(address)
                            checkAndAppendAddress(zip)
                        }
                    }

                    tvAddress.text = companyAddress.toString()
                }
            }
        })
    }

    private fun FragmentSummaryBinding.createWebsiteClickableSpan(url: String?) {
        url?.let {
            val ss = SpannableString(it)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.linkColor = ContextCompat.getColor(tvWebsite.context, R.color.color_black)
                    ds.isUnderlineText = true
                    super.updateDrawState(ds)
                }
            }
            ss.setSpan(clickableSpan, 0, url.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvWebsite.text = ss
            tvWebsite.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun FragmentSummaryBinding.loadLogo(url: String?) {
        Glide
            .with(image)
            .load(url)
            .priority(Priority.HIGH)
            .transform(CenterCrop(), RoundedCorners(26))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(image)
    }
}