package com.example.employeeapp2

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_employee_detail.*
import kotlinx.android.synthetic.main.fragment_employee_detail.employee_role
import kotlinx.android.synthetic.main.fragment_employee_list.*
import kotlinx.android.synthetic.main.fragment_employee_list.toolbar_show
import kotlinx.android.synthetic.main.fragment_show_employee.*
import kotlinx.android.synthetic.main.list_item.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

const val PERMISSION_REQUEST_CAMERA = 0
const val CAMERA_PHOTO_REQUEST = 1
const val GALLERY_PHOTO_REQUEST = 2

const val LATEST_EMPLOYEE_NAME_KEY = "LATEST_EMPLOYEE_NAME"

/**
 * A simple [Fragment] subclass.
 * Use the [EmployeeDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmployeeDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: EmployeeDetailViewModel
    private var selectedPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(EmployeeDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar_detail
            .setupWithNavController(navController, appBarConfiguration)

        toolbar_detail.inflateMenu(R.menu.detail_menu)
        toolbar_detail.setOnMenuItemClickListener {
            handleMenuItem(it)
        }

        val roles = resources.getStringArray(R.array.role)
        val roleAdapter = ArrayAdapter(requireContext(), R.layout.category_items, roles)
        employee_role.setAdapter(roleAdapter)

        val ages = mutableListOf<Int>()
        for(i in 18 until 81) {
            ages.add(i)
        }
        val ageAdapter = ArrayAdapter(requireContext(), R.layout.category_items, ages)
        employee_age2.setAdapter(ageAdapter)

        val id = EmployeeDetailFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setEmployeeId(id)

        viewModel.employee.observe(viewLifecycleOwner, Observer {
            it?.let {
                setData(it)
            }
        })

        save_employee.setOnClickListener {
            saveEmployee()
        }

        delete_employee.setOnClickListener {
            deleteEmployee()
        }

        //reset image
        employee_photo2.setOnClickListener {
            employee_photo2.setImageResource(R.drawable.user)
            employee_photo2.tag = ""
        }

        photo_from_camera.setOnClickListener {
            clickPhotoAfterPermission(it)
            Log.d(TAG, "Hello")
        }

        photo_from_gallery.setOnClickListener {
            pickPhoto()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when(item.itemId) {
//            R.id.menu_share_data -> {
//                shareEmployeeData()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun handleMenuItem(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_share_data -> {
                shareEmployeeData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun shareEmployeeData() {
        val name = employee_name2.text.toString()
        val role = employee_role.text.toString()
        val age = employee_age2.text.toString()

        val selectedStatusButton = gender_group.findViewById<RadioButton>(gender_group.checkedRadioButtonId)
        val gender = selectedStatusButton.text

        val shareText = getString(R.string.share_text, name, role, age, gender)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Choose the app to send data")
        startActivity(shareIntent)
    }

    private fun setData(employee: Employee) {
        with(employee.photo){
            if(isNotEmpty()){
                employee_photo2.setImageURI(Uri.parse(this))
                employee_photo2.tag = this

            } else{
                employee_photo2.setImageResource(R.drawable.user)
                employee_photo2.tag = ""
            }
        }
        employee_name2.setText(employee.name)
        employee_age2.setText(employee.age.toString(), false)
        employee_role.setText(employee.role, false)

        when(employee.gender) {
            Gender.Male.ordinal -> {
                gender_male.isChecked = true
            }
            Gender.Female.ordinal -> {
                gender_female.isChecked = true
            }
            else -> {
                gender_other.isChecked = true
            }
        }
    }

    private fun saveEmployee() {
        val name = employee_name2.text.toString()
        val role = employee_role.text.toString()
        val age = employee_age2.text.toString().toInt()

        val selectedStatusButton = gender_group.findViewById<RadioButton>(gender_group.checkedRadioButtonId)
        var gender = Gender.Other.ordinal
        if(selectedStatusButton.text == Gender.Male.name) {
            gender = Gender.Male.ordinal
        } else if(selectedStatusButton.text == Gender.Female.name) {
            gender = Gender.Female.ordinal
        }

        var photo = ""

        employee_photo2.tag?.let {
            photo = it as String
        }

        val employee = Employee(viewModel.employeeId.value!!, name, role, age, gender, photo)
        viewModel.saveEmployee(employee)

        if(viewModel.employeeId.value == 0L) {
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString(LATEST_EMPLOYEE_NAME_KEY, name)
                commit()
            }
        }

        requireActivity().onBackPressed()
    }

    private fun deleteEmployee() {
        viewModel.deleteEmployee()
        requireActivity().onBackPressed()
    }

    private fun clickPhotoAfterPermission(view: View) {
        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            clickPhoto()
        } else {
            requestCameraPermission(view)
        }
    }

    private fun requestCameraPermission(view: View) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            val snack = Snackbar.make(view, "We need your permission to take a photo. When asked please give the permission",
                Snackbar.LENGTH_INDEFINITE)
            snack.setAction("OK", View.OnClickListener {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
            })
            snack.show()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_CAMERA) {
            if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                clickPhoto()
            } else {
                Toast.makeText(requireActivity(), "Permission denied to use camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clickPhoto(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createFile(requireActivity(), Environment.DIRECTORY_PICTURES, "jpg")
                } catch (ex: IOException) {
                    Toast.makeText(requireActivity(), getString(R.string.create_file_Error, ex.message),
                        Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    selectedPhotoPath = it.absolutePath
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_PHOTO_REQUEST)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){
            when(requestCode){
                CAMERA_PHOTO_REQUEST -> {
                    val uri = Uri.fromFile(File(selectedPhotoPath))
                    employee_photo2.setImageURI(uri)
                    employee_photo2.tag = uri.toString()
                }
                GALLERY_PHOTO_REQUEST ->{
                    data?.data?.also { uri ->
                        val photoFile: File? = try {
                            createFile(requireActivity(), Environment.DIRECTORY_PICTURES, "jpg")
                        } catch (ex: IOException) {
                            Toast.makeText(requireActivity(), getString(R.string.create_file_Error, ex.message),
                                Toast.LENGTH_SHORT). show()
                            null
                        }
                        photoFile?.also {
                            try {
                                val resolver = requireActivity().contentResolver
                                resolver.openInputStream(uri).use { stream ->
                                    val output = FileOutputStream(photoFile)
                                    stream!!.copyTo(output)
                                }
                                val fileUri = Uri.fromFile(photoFile)
                                employee_photo2.setImageURI(fileUri)
                                employee_photo2.tag = fileUri.toString()
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun pickPhoto(){
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(pickPhotoIntent, GALLERY_PHOTO_REQUEST)
        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EmployeeDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmployeeDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        const val TAG = "MainActivity"
    }
}