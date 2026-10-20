package com.example.snapclean

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private val friendChoices = linkedMapOf<String, CheckBox>()
    private val chatChoices = linkedMapOf<String, CheckBox>()
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(23, 23, 23)
        window.navigationBarColor = Color.rgb(23, 23, 23)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            setBackgroundColor(Color.rgb(250, 249, 246))
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)

        content.addView(label("👻  SnapClean", 28f, Color.rgb(25, 25, 25), true), matchWrap())
        content.addView(label("Friend and chat cleanup assistant", 15f, Color.DKGRAY, false), marginParams(0, 4, 0, 14))
        content.addView(label("Important: demo selections only. SnapClean cannot access your Snapchat account or perform bulk removals/deletions. Review and make changes inside Snapchat.", 14f, Color.rgb(110, 70, 25), false), marginParams(0, 0, 0, 22))

        content.addView(label("1. Select friends to review", 20f, Color.BLACK, true), marginParams(0, 0, 0, 4))
        content.addView(label("Choose categories of people you may want to unfriend.", 14f, Color.GRAY, false), marginParams(0, 0, 0, 8))
        listOf("Inactive friends", "People I don't recognize", "Old contacts", "Other friends").forEach { title ->
            addChoice(content, title, friendChoices)
        }

        content.addView(label("2. Select chats to review", 20f, Color.BLACK, true), marginParams(0, 22, 0, 4))
        content.addView(label("Choose chat categories you may want to clear or remove.", 14f, Color.GRAY, false), marginParams(0, 0, 0, 8))
        listOf("Old conversations", "Inactive chats", "Unwanted conversations", "Other chats").forEach { title ->
            addChoice(content, title, chatChoices)
        }

        val review = Button(this).apply {
            text = "Review selected friends and chats"
            textSize = 16f
            isAllCaps = false
            setTextColor(Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.rgb(108, 76, 241))
            setOnClickListener { showReview() }
        }
        content.addView(review, marginParams(0, 24, 0, 12))
        status = label("No changes have been made to Snapchat.", 13f, Color.GRAY, false)
        content.addView(status, marginParams(0, 8, 0, 12))

        val help = Button(this).apply {
            text = "Open Snapchat Help"
            textSize = 15f
            isAllCaps = false
            setOnClickListener { openHelp() }
        }
        content.addView(help, matchWrap())
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun addChoice(parent: LinearLayout, title: String, target: LinkedHashMap<String, CheckBox>) {
        val cb = CheckBox(this).apply {
            text = title
            textSize = 16f
            setTextColor(Color.rgb(35, 35, 35))
            buttonTintList = android.content.res.ColorStateList.valueOf(Color.rgb(108, 76, 241))
            setPadding(dp(4), dp(8), dp(4), dp(8))
        }
        target[title] = cb
        parent.addView(cb, matchWrap())
    }

    private fun showReview() {
        val friends = friendChoices.filterValues { it.isChecked }.keys
        val chats = chatChoices.filterValues { it.isChecked }.keys
        if (friends.isEmpty() && chats.isEmpty()) {
            status.text = "Select at least one friend category or chat category to review."
            return
        }
        val message = buildString {
            if (friends.isNotEmpty()) {
                append("Friend categories to review:\n")
                append(friends.joinToString("\n", prefix = "• "))
                append("\n\n")
            }
            if (chats.isNotEmpty()) {
                append("Chat categories to review:\n")
                append(chats.joinToString("\n", prefix = "• "))
                append("\n\n")
            }
            append("These are labels only, not your actual Snapchat friends or conversations. SnapClean cannot read your friend list, inspect chats, unfriend people, or delete chats. Open Snapchat and make any changes there after checking carefully.")
        }
        android.app.AlertDialog.Builder(this)
            .setTitle("Review before making changes")
            .setMessage(message)
            .setNegativeButton("Go back", null)
            .setPositiveButton("Open Snapchat Help") { _, _ -> openHelp() }
            .show()
    }

    private fun openHelp() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://help.snapchat.com/")))
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean): TextView = TextView(this).apply {
        this.text = text
        textSize = size
        setTextColor(color)
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun marginParams(left: Int, top: Int, right: Int, bottom: Int) = LinearLayout.LayoutParams(-1, -2).apply {
        setMargins(dp(left), dp(top), dp(right), dp(bottom))
    }
}
