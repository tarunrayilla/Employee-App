package com.example.employeeapp2

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null) {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToEmployeeListFragment()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_link.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }

        login.setOnClickListener {
            email_layout_login.error = null
            password_layout_login.error = null

            val email = email_login.text.toString()
            val pass = password_login.text.toString()

            if(validateInput(email, pass)) {
                progressBar.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(requireActivity()) { task ->
                        progressBar.visibility = View.INVISIBLE
                        if(task.isSuccessful) {
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToEmployeeListFragment()
                            )
                        } else {
                            val toast = Toast.makeText(requireActivity(),
                                "Authentication Failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                            toast.show()
                        }
                }
            }
        }
    }

    private fun validateInput(email: String, pass: String): Boolean {
        var valid = true
        if(email.isBlank()) {
            email_layout_login.error = "Please enter an email address"
            valid = false
        }
        if(pass.isBlank()) {
            password_layout_login.error = "Please enter password"
            valid = false
        }

        return valid
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}