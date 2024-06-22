package eu.application.twotowers.onboardingscreen

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.application.twotowers.databinding.Onboardingpage1Binding
import eu.application.twotowers.databinding.Onboardingpage1BindingImpl
import eu.application.twotowers.databinding.Onboardingpage2Binding
import eu.application.twotowers.databinding.Onboardingpage2BindingImpl
import eu.application.twotowers.databinding.Onboardingpage3Binding
import eu.application.twotowers.databinding.Onboardingpage3BindingImpl
import eu.application.twotowers.registration.LoginActivity

class OnboardingPagerAdapter(private val context: Context, private val pages: List<OnboardingPage>,
                             private val onboardingCompletedCallback: () -> Unit) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PAGE_1 = 1
        private const val TYPE_PAGE_2 = 2
        private const val TYPE_PAGE_3 = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            TYPE_PAGE_1 -> {
                val binding = Onboardingpage1BindingImpl.inflate(inflater, parent, false)
                Page1ViewHolder(binding)
            }
            TYPE_PAGE_2 -> {
                val binding = Onboardingpage2BindingImpl.inflate(inflater, parent, false)
                Page2ViewHolder(binding)
            }
            TYPE_PAGE_3 -> {
                val binding = Onboardingpage3BindingImpl.inflate(inflater, parent, false)
                Page3ViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val page = pages[position]
        when (holder) {
            is Page1ViewHolder -> holder.bind(page)
            is Page2ViewHolder -> holder.bind(page)
            is Page3ViewHolder -> holder.bind(page)
        }
        if (position == itemCount - 1) {
            onboardingCompletedCallback.invoke()
        }
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun getItemViewType(position: Int): Int {
        // Return appropriate view type based on position
        return when (position) {
            0 -> TYPE_PAGE_1
            1 -> TYPE_PAGE_2
            2 -> TYPE_PAGE_3
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    inner class Page1ViewHolder(private val binding: Onboardingpage1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: OnboardingPage) {
            binding.page = page
            binding.executePendingBindings()
        }
    }

    inner class Page2ViewHolder(private val binding: Onboardingpage2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: OnboardingPage) {
            binding.page = page
            binding.executePendingBindings()
        }
    }

    inner class Page3ViewHolder(private val binding: Onboardingpage3Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: OnboardingPage) {
            binding.page = page
            binding.nextPage.setOnClickListener {
                val intent = Intent(context ,LoginActivity::class.java )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
            binding.executePendingBindings()

        }
    }
}
