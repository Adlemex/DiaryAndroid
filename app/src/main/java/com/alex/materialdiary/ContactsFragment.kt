package com.alex.materialdiary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alex.materialdiary.databinding.FragmentMsgContactsBinding
import com.alex.materialdiary.sys.messages.API
import com.alex.materialdiary.sys.adapters.ProgramAdapterContacts


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ContactsFragment : Fragment(), API.Callback_Contacts {
    private var _binding: FragmentMsgContactsBinding? = null


    var cookies: String = ""
    lateinit var api: API
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMsgContactsBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("cks", CookieManager.getInstance().getCookie("one.pskovedu.ru"))
        println("")
        cookies = CookieManager.getInstance().getCookie("one.pskovedu.ru")
        val api = API.getInstance(cookies)
        api.GetContacts(this)
        binding.floatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.to_add_contact)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun Contacts(
        logins: MutableList<String>?,
        names: MutableList<String>?,
        unreaded: MutableList<Int>?,
        isGroup: MutableList<Boolean>?
    ) {
        val listView: ListView = binding.ContactsList
        val adapter =
            ProgramAdapterContacts(
                this,
                logins,
                names,
                unreaded,
                isGroup
            )
        listView.adapter = adapter
    }

    public fun openMsg(name: String, login: String, isGroup: Boolean){
        //api.GetMessages(this, name, login, isGroup)
        //findNavController().navigate()
        val action = ChatFragmentDirections.toChat(name, login, isGroup)
        findNavController().navigate(action)

    }
}