package com.backend.bms.security.services

import com.backend.bms.models.user.User
import com.backend.bms.repositories.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository;

    @Transactional
    @Throws( UsernameNotFoundException::class )
    override fun loadUserByUsername( username: String ): UserDetails {
        val user: User = userRepository.findByUsername( username )
            .orElseThrow { UsernameNotFoundException( "User doesn't exist with given username: $username" ) }
        return UserDetailsImpl.build( user );
    }
}