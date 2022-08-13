package com.onixen.semicircularrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.onixen.semicircular_layout_manager.SemicircularLayoutManager
import com.onixen.semicircularrecyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var binding: ActivityMainBinding

    private var selectedItem: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8")

        binding.recyclerView.apply {
            adapter = RecyclerAdapter(list = list) { position ->
                if (selectedItem != position) {
                    this.smoothScrollToPosition(position)
                    selectedItem = position
                    binding.selectedItem.text = list[position]
                }
            }
            layoutManager = SemicircularLayoutManager(
                itemViewWidthDp = 100,
                countVisibleItems = 3f,
                millisecondsPerPx = 100f
            )
        }
    }
}