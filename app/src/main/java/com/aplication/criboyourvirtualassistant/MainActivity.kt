package com.aplication.criboyourvirtualassistant

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var sendBtn: ImageButton;
    lateinit var editText: EditText;
    lateinit var messagesList: MessagesList;
    lateinit var us: User;
    lateinit var chatGPT: User;
    lateinit var adapter: MessagesListAdapter<Messages>


    //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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // block screen orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        sendBtn = findViewById(R.id.imageButton);
        editText = findViewById(R.id.editTextTextPersonName);
        messagesList = findViewById(R.id.messagesList)

        var imageLoader: ImageLoader = object : ImageLoader {
            override fun loadImage(imageView: ImageView?, url: String?, payload: Any?) {

                Picasso.get().load(url).into(imageView);
            }
        }




        adapter = MessagesListAdapter<Messages>("1", imageLoader)

        //Call the copyToClipboard method when the user long presses on a message.
        adapter.setOnMessageLongClickListener { message ->
            copyToClipboard(message.text)
            true

            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }



        messagesList.setAdapter(adapter)
        us = User("1", "Cristi", "")
        chatGPT = User("2", "ChatGpt", "")



        sendBtn.setOnClickListener {

            var message: Messages =
                Messages("m1", editText.text.toString(), us, Calendar.getInstance().time, "")
            adapter.addToStart(message, true,)

            if (editText.text.toString().startsWith("generează o imagine")

            ) {
                generateImages(editText.text.toString())
            } else {
                performAction(editText.text.toString())
            }
            editText.text.clear()

        }

    }

    private fun copyToClipboard(content: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", content)
        clipboardManager.setPrimaryClip(clipData)
    }

    //funciton that return text response from chatGpt
    fun performAction(input: String) {

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openai.com/v1/completions"


        val jsonObject = JSONObject()
        jsonObject.put("prompt", input)
        jsonObject.put("model", "text-davinci-003")
        jsonObject.put("temperature", 0)
        jsonObject.put("max_tokens", 3000)

// Request a string response from the provided URL.
        val stringRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonObject,

            Response.Listener<JSONObject> { response ->
                // Display the first 500 characters of the response string.
                var answer = response.getJSONArray("choices").getJSONObject(0).getString("text")


                var message: Messages =
                    Messages("m2", answer.trim(), chatGPT, Calendar.getInstance().time, "")
                adapter.addToStart(message, true,)
            },
            Response.ErrorListener {}) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map.put("Content-Type", "application/json")
                map.put(
                    "Authorization",
                    "Bearer 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"
                )
                return map
            }
        }


        stringRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 60000;
            }

            override fun getCurrentRetryCount(): Int {
                return 15;
            }

            override fun retry(error: VolleyError?) {

            }
        })

        // hide soft keyboard
        if (editText != null) {
            // on below line we are creating a variable
            // for input manager and initializing it.
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            // on below line hiding our keyboard.
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0)

            //Add the request to the RequestQueue.
            queue.add(stringRequest)


        }


    }

    //funciton that return image response from chatGpt
    fun generateImages(input: String) {

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openai.com/v1/images/generations"


        val jsonObject = JSONObject()
        jsonObject.put("prompt", input)
        jsonObject.put("n", 4)
        jsonObject.put("size", "1024x1024")

// Request a string response from the provided URL.
        val stringRequest = object : JsonObjectRequest(Request.Method.POST,
            url,
            jsonObject,
            Response.Listener<JSONObject> { response ->
                // Display the first 500 characters of the response string.
                var jsonArray = response.getJSONArray("data")
                for (i in 0 until jsonArray.length() - 1) {
                    var answer = jsonArray.getJSONObject(i).getString("url")
                    var message: Messages =
                        Messages("m2", "image", chatGPT, Calendar.getInstance().time, answer)
                    adapter.addToStart(message, true,)
                }

            },
            Response.ErrorListener {}) {
            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map.put("Content-Type", "application/json")
                map.put(
                    "Authorization",
                    "Bearer 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"
                )
                return map
            }
        }


        stringRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 60000;
            }

            override fun getCurrentRetryCount(): Int {
                return 15;
            }

            override fun retry(error: VolleyError?) {

            }
        })
        // hide soft keyboard
        if (editText != null) {
            // on below line we are creating a variable
            // for input manager and initializing it.
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            // on below line hiding our keyboard.
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0)
        }

        // hide soft keyboard
        if (editText != null)
        {
            // on below line we are creating a variable
            // for input manager and initializing it.
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            // on below line hiding our keyboard.
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0)
        }
        //Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

}



