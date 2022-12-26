package de.martin.vocabularcardapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.martin.vocabularcardapp.R
import de.martin.vocabularcardapp.VocabSheet


class VocabAdapter :
    RecyclerView.Adapter<VocabAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.list_item_card)
        val vocabOne: Button = itemView.findViewById(R.id.list_item_vocab_one)
        val vocabTwo: Button = itemView.findViewById(R.id.list_item_vocab_two)
        val vocabThird: Button = itemView.findViewById(R.id.list_item_vocab_third)
    }

    private var lastPosition = -1
    var vocabList: MutableList<VocabSheet> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vocab = vocabList[position]
        //So animation doesnt play if already visible
        if (position > lastPosition) {
            setAnimation(holder.card.context, holder.itemView)
            lastPosition = position
        }
        holder.vocabOne.text = vocab.firstVocab
        holder.vocabOne.isActivated = true
        holder.vocabTwo.text = "Hangeul"
        holder.vocabTwo.isSelected = false
        holder.vocabThird.text = "Korean"
        holder.vocabThird.isSelected = false
        holder.vocabTwo.setOnClickListener {
            holder.vocabTwo.text = vocab.secondVocab
            holder.vocabTwo.isSelected = true
        }
        holder.vocabThird.setOnClickListener {
            holder.vocabThird.text = vocab.thirdVocab
            holder.vocabThird.isSelected = true
        }
    }

    private fun setAnimation(context: Context, itemView: View) {
        val animation: Animation =
            AnimationUtils.loadAnimation(context, R.anim.recycler_anim)
        itemView.startAnimation(animation)
    }

    override fun getItemCount(): Int {
        return vocabList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: MutableList<VocabSheet>?) {
        if (list != null) {
            vocabList = list
            notifyDataSetChanged()
        }
    }

    fun remove(position: Int) {
        vocabList.removeAt(position)
        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, vocabList.size)
        notifyItemRangeChanged(position, 1)
    }
}
