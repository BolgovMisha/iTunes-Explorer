package apps.mishka.testapp.di

import apps.mishka.testapp.ui.search.SearchFragment
import dagger.Component

@Component(modules = [FragmentModule::class])
interface FragmentComponent {
    fun inject(searchFragment: SearchFragment)
}